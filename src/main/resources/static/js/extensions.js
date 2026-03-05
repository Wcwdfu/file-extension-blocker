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


// 공통 액션 실행 헬퍼
async function runAction(fn, successMsg = null) {
    try {
        showMsg("");
        await fn();
        await loadAll();
        if (successMsg) showMsg(successMsg, true);
    } catch (e) {
        showMsg(e.message, false);
        throw e;
    }
}

async function loadAll() {
    const data = await api("/api/extensions");
    renderFixed(data.fixed);
    renderCustom(data.custom);
    countTextEl.textContent = `(${data.customCount} / 200)`;
}

// 고정 확장자 렌더링 & 상태 변경
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
            cb.disabled = true;

            try {
                await runAction(() => api(`/api/extensions/${item.id}`, {
                    method: "PATCH",
                    body: JSON.stringify({ blocked: target })
                }));
            } catch (err) {
                // 실패 시 UI 원복
                cb.checked = !target;
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

// 커스텀 확장자 렌더링 & 삭제
function renderCustom(items) {
    customListEl.innerHTML = "";

    items.forEach(item => {
        const chip = document.createElement("div");
        chip.className = "chip";

        const label = document.createElement("span");
        label.textContent = item.extension;

        const x = document.createElement("button");
        x.className = "x";
        x.type = "button";
        x.textContent = "X";

        x.addEventListener("click", async () => {
            x.disabled = true;
            try {
                await runAction(
                    () => api(`/api/extensions/custom/${item.id}`, { method: "DELETE" })
                );
            } finally {
                x.disabled = false;
            }
        });

        chip.appendChild(label);
        chip.appendChild(x);
        customListEl.appendChild(chip);
    });
}

// 커스텀 확장자 추가
addBtn.addEventListener("click", async () => {
    addBtn.disabled = true;

    try {
        await runAction(async () => {
            const ext = normalizeClient(inputEl.value);

            if (!ext) throw new Error("확장자를 입력해주세요.");
            if (!EXT_RE.test(ext)) throw new Error("확장자는 영어, 숫자, .만 입력 가능합니다.");
            if (ext.length > 20) throw new Error("확장자는 20자 이하여야 합니다.");

            await api("/api/extensions/custom", {
                method: "POST",
                body: JSON.stringify({ extension: ext })
            });

            inputEl.value = "";
        }, "추가 완료");
    } finally {
        addBtn.disabled = false;
    }
});

// Enter로 추가
inputEl.addEventListener("keydown", (e) => {
    if (e.key === "Enter") addBtn.click();
});

// 전체 초기화
resetBtn.addEventListener("click", async () => {
    const ok = confirm(
        "정말 초기화하시겠습니까?\n- 커스텀 확장자는 모두 삭제됩니다.\n- 고정 확장자는 모두 해제됩니다."
    );
    if (!ok) return;

    resetBtn.disabled = true;
    try {
        await runAction(
            () => api("/api/extensions/reset", { method: "POST" }),
            "초기화 완료"
        );
    } finally {
        resetBtn.disabled = false;
    }
});

// 최초 로딩
loadAll().catch(e => showMsg(e.message, false));