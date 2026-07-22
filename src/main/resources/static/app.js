const tokenKey = "financeApiToken";

const state = {
    token: localStorage.getItem(tokenKey),
    user: null,
};

const el = {
    authView: document.querySelector("#authView"),
    dashboardView: document.querySelector("#dashboardView"),
    loginForm: document.querySelector("#loginForm"),
    registerForm: document.querySelector("#registerForm"),
    transactionForm: document.querySelector("#transactionForm"),
    logoutButton: document.querySelector("#logoutButton"),
    refreshButton: document.querySelector("#refreshButton"),
    userName: document.querySelector("#userName"),
    userEmail: document.querySelector("#userEmail"),
    balanceValue: document.querySelector("#balanceValue"),
    incomeValue: document.querySelector("#incomeValue"),
    expenseValue: document.querySelector("#expenseValue"),
    countValue: document.querySelector("#countValue"),
    transactionsTable: document.querySelector("#transactionsTable"),
    emptyMessage: document.querySelector("#emptyMessage"),
    message: document.querySelector("#message"),
};

const currency = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
});

async function api(path, options = {}) {
    const response = await fetch(path, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(state.token ? { Authorization: `Bearer ${state.token}` } : {}),
            ...options.headers,
        },
    });

    const text = await response.text();
    const body = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(readError(body, response.status));
    }

    return body;
}

function readError(body, status) {
    if (!body) {
        return `Erro ${status}`;
    }

    if (body.error) {
        return body.error;
    }

    return Object.values(body).join(" ");
}

function showMessage(text, error = false) {
    el.message.textContent = text;
    el.message.classList.toggle("error", error);
    el.message.hidden = false;

    window.clearTimeout(showMessage.timeout);
    showMessage.timeout = window.setTimeout(() => {
        el.message.hidden = true;
    }, 3000);
}

function formData(form) {
    return Object.fromEntries(new FormData(form).entries());
}

function setLoggedIn(token, user) {
    state.token = token;
    state.user = user;
    localStorage.setItem(tokenKey, token);
    renderAuthState();
}

function logout() {
    state.token = null;
    state.user = null;
    localStorage.removeItem(tokenKey);
    renderAuthState();
}

function renderAuthState() {
    const logged = Boolean(state.token && state.user);

    el.authView.hidden = logged;
    el.dashboardView.hidden = !logged;
    el.logoutButton.hidden = !logged;

    if (logged) {
        el.userName.textContent = state.user.name;
        el.userEmail.textContent = state.user.email;
    }
}

async function loadDashboard() {
    state.user = await api("/users/me");
    renderAuthState();

    const [summary, transactions] = await Promise.all([
        api("/transactions/summary"),
        api("/transactions"),
    ]);

    renderSummary(summary);
    renderTransactions(transactions);
}

function renderSummary(summary) {
    el.balanceValue.textContent = money(summary.balance);
    el.incomeValue.textContent = money(summary.totalIncome);
    el.expenseValue.textContent = money(summary.totalExpense);
    el.countValue.textContent = summary.transactionCount;
}

function renderTransactions(transactions) {
    el.transactionsTable.innerHTML = "";
    el.emptyMessage.hidden = transactions.length > 0;

    for (const transaction of transactions) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${safe(transaction.description)}</td>
            <td>${safe(transaction.category)}</td>
            <td>${formatDate(transaction.date)}</td>
            <td>${transaction.type === "INCOME" ? "Entrada" : "Saida"}</td>
            <td>${money(transaction.amount)}</td>
        `;
        el.transactionsTable.appendChild(row);
    }
}

function money(value) {
    return currency.format(Number(value || 0));
}

function formatDate(value) {
    return value ? value.split("-").reverse().join("/") : "";
}

function safe(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

el.loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    try {
        const auth = await api("/auth/login", {
            method: "POST",
            body: JSON.stringify(formData(el.loginForm)),
        });
        setLoggedIn(auth.token, auth.user);
        await loadDashboard();
    } catch (error) {
        showMessage(error.message, true);
    }
});

el.registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    try {
        const auth = await api("/auth/register", {
            method: "POST",
            body: JSON.stringify(formData(el.registerForm)),
        });
        setLoggedIn(auth.token, auth.user);
        await loadDashboard();
    } catch (error) {
        showMessage(error.message, true);
    }
});

el.transactionForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    try {
        const data = formData(el.transactionForm);
        data.amount = Number(data.amount);

        await api("/transactions", {
            method: "POST",
            body: JSON.stringify(data),
        });

        el.transactionForm.reset();
        setToday();
        await loadDashboard();
        showMessage("Transacao salva.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

el.logoutButton.addEventListener("click", logout);

el.refreshButton.addEventListener("click", async () => {
    try {
        await loadDashboard();
        showMessage("Dados atualizados.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

function setToday() {
    el.transactionForm.date.value = new Date().toISOString().slice(0, 10);
}

setToday();

if (state.token) {
    loadDashboard().catch(() => logout());
} else {
    renderAuthState();
}
