const fixedListEl = document.getElementById("fixedList");
const customListEl = document.getElementById("customList");
const inputEl = document.getElementById("extInput");
const addBtn = document.getElementById("addBtn");
const msgEl = document.getElementById("msg");
const countTextEl = document.getElementById("countText");
const resetBtn = document.getElementById("resetBtn");

function showMsg(text, ok = false) {
    msgEl.className = text ? (ok ? "ok" : "error") : "";
    msgEl.textContent = text || "";
}

async function api(url, options = {}) {
    const res = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options,
    });

    if (!res.ok) {
        let data;
        try { data = await res.json(); } catch (e) {}
        throw new Error(data?.message || `요청 실패 (${res.status})`);
    }

    if (res.status === 204) return null;
    const text = await res.text();
    return text ? JSON.parse(text) : null;
}

const normalizeClient = (v) => v.trim().toLowerCase();
const EXT_RE = /^[a-z0-9]+(\.[a-z0-9]+)*$/;

async function loadAll() {
    const data = await api("/api/extensions");
    renderFixed(data.fixed);
    renderCustom(data.custom);
    countTextEl.textContent = `(${data.customCount} / 200)`;
}

async function runAction(fn) {
    try {
        showMsg("");
        await fn();
        await loadAll();
    } catch (e) {
        showMsg(e.message, false);
    }
}

// 고정 확장자 렌더링 & 상태변경

function renderFixed(items) {
    fixedListEl.innerHTML = "";

    items.forEach(item => {
        const wrapper = document.createElement("label");
        wrapper.className = "fixed-item";

        const cb = document.createElement("input");
        cb.type = "checkbox";
        cb.checked = item.blocked;

        cb.addEventListener("change", async () => {
            const target = cb.checked;

            try {
                showMsg("");
                cb.disabled = true;

                await api(`/api/extensions/${item.id}`, {
                    method: "PATCH",
                    body: JSON.stringify({ blocked: target })
                });

                await loadAll();
            } catch (err) {
                cb.checked = !target;
                showMsg(err.message, false);
            } finally {
                cb.disabled = false;
            }
        });

        const text = document.createElement("span");
        text.textContent = item.extension;

        wrapper.appendChild(cb);
        wrapper.appendChild(text);
        fixedListEl.appendChild(wrapper);
    });
}

// 커스텀 확장자 렌더링 & 상태변경

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

        x.addEventListener("click", () =>
            runAction(() => api(`/api/extensions/custom/${item.id}`, { method: "DELETE" }))
        );

        chip.appendChild(label);
        chip.appendChild(x);
        customListEl.appendChild(chip);
    });
}

// 커스텀 확장자 추가
addBtn.addEventListener("click", () => runAction(async () => {
    const ext = normalizeClient(inputEl.value);

    if (!ext) throw new Error("확장자를 입력해주세요.");
    if (!EXT_RE.test(ext)) throw new Error("확장자는 영어, 숫자, .만 입력 가능합니다.");
    if (ext.length > 20) throw new Error("확장자는 20자 이하여야 합니다.");

    await api("/api/extensions/custom", {
        method: "POST",
        body: JSON.stringify({ extension: ext })
    });

    inputEl.value = "";
    showMsg("추가 완료", true);
}));

// 초기화 버튼
resetBtn.addEventListener("click", async () => {
    const ok = confirm("정말 초기화하시겠습니까?\n- 커스텀 확장자는 모두 삭제됩니다.\n- 고정 확장자는 모두 해제됩니다.");
    if (!ok) return;

    try {
        showMsg("");
        resetBtn.disabled = true;

        await api("/api/extensions/reset", { method: "POST" }); // 또는 DELETE로 해도 됨
        showMsg("초기화 완료", true);
        await loadAll();

    } catch (e) {
        showMsg(e.message, false);
    } finally {
        resetBtn.disabled = false;
    }
});

//

inputEl.addEventListener("keydown", (e) => {
    if (e.key === "Enter") addBtn.click();
});

loadAll().catch(e => showMsg(e.message, false));