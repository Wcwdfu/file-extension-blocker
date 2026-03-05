const fixedListEl = document.getElementById("fixedList");
const customListEl = document.getElementById("customList");
const inputEl = document.getElementById("extInput");
const addBtn = document.getElementById("addBtn");
const msgEl = document.getElementById("msg");
const countTextEl = document.getElementById("countText");

function showMsg(text, ok = false) {
    msgEl.className = ok ? "ok" : "error";
    msgEl.textContent = text;
    if (!text) msgEl.className = "";
}

async function api(url, options = {}) {
    const res = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options,
    });

    if (!res.ok) {
        let data;
        try { data = await res.json(); } catch (e) {}
        const msg = data?.message || `요청 실패 (${res.status})`;
        throw new Error(msg);
    }

    if (res.status === 204) return null;
    const text = await res.text();
    return text ? JSON.parse(text) : null;
}

function normalizeClient(v) {
    return v.trim().toLowerCase();
}

async function refreshCount() {
    const count = await api("/api/extensions/custom/count");
    countTextEl.textContent = `(${count} / 200)`;
}

function renderFixed(items) {
    fixedListEl.innerHTML = "";
    items.forEach(item => {
        const wrapper = document.createElement("label");
        wrapper.className = "fixed-item";

        const cb = document.createElement("input");
        cb.type = "checkbox";
        cb.checked = item.blocked;

        cb.addEventListener("change", async () => {
            try {
                showMsg("");
                await api(`/api/extensions/${item.id}/toggle`, { method: "PATCH" });
            } catch (e) {
                cb.checked = !cb.checked;
                showMsg(e.message, false);
            }
        });

        const text = document.createElement("span");
        text.textContent = item.extension;

        wrapper.appendChild(cb);
        wrapper.appendChild(text);
        fixedListEl.appendChild(wrapper);
    });
}

function renderCustom(items) {
    customListEl.innerHTML = "";
    items.forEach(item => {
        const chip = document.createElement("div");
        chip.className = "chip";

        const label = document.createElement("span");
        label.textContent = item.extension;

        const x = document.createElement("button");
        x.className = "x";
        x.textContent = "X";

        x.addEventListener("click", async () => {
            try {
                showMsg("");
                await api(`/api/extensions/custom/${item.id}`, { method: "DELETE" });
                await loadAll();
            } catch (e) {
                showMsg(e.message, false);
            }
        });

        chip.appendChild(label);
        chip.appendChild(x);
        customListEl.appendChild(chip);
    });
}

async function loadAll() {
    const [fixed, custom] = await Promise.all([
        api("/api/extensions/fixed"),
        api("/api/extensions/custom"),
    ]);
    renderFixed(fixed);
    renderCustom(custom);
    await refreshCount();
}

addBtn.addEventListener("click", async () => {
    try {
        showMsg("");

        const raw = inputEl.value;
        const ext = normalizeClient(raw);

        if (!ext) {
            showMsg("확장자를 입력해주세요.", false);
            return;
        }
        if (!/^[a-zA-Z]+$/.test(ext)) {
            showMsg("확장자는 영어만 입력 가능합니다.", false);
            return;
        }
        if (ext.length > 20) {
            showMsg("확장자는 20자 이하여야 합니다.", false);
            return;
        }

        await api("/api/extensions/custom", {
            method: "POST",
            body: JSON.stringify({ extension: ext })
        });

        inputEl.value = "";
        showMsg("추가 완료", true);
        await loadAll();

    } catch (e) {
        showMsg(e.message, false);
    }
});

inputEl.addEventListener("keydown", (e) => {
    if (e.key === "Enter") addBtn.click();
});

loadAll().catch(e => showMsg(e.message, false));
