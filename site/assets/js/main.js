function copyButton(button, getText) {
  button.addEventListener("click", async () => {
    try {
      await navigator.clipboard.writeText(getText());
      button.textContent = "Copied";
    } catch {
      button.textContent = "Press Ctrl+C";
    }
    setTimeout(() => (button.textContent = "Copy"), 1500);
  });
}

document.querySelectorAll("[data-copy]").forEach((button) => {
  copyButton(button, () => button.previousElementSibling.textContent.trim());
});

document.querySelectorAll(".prose div.highlight").forEach((block) => {
  const button = document.createElement("button");
  button.type = "button";
  button.className = "copy";
  button.textContent = "Copy";
  button.setAttribute("aria-label", "Copy code");
  copyButton(button, () => block.querySelector("pre").textContent.trim());
  block.append(button);
});

document.querySelectorAll("[data-showcase]").forEach((showcase) => {
  const show = () => {
    const { scene, lang } = showcase.dataset;
    showcase.querySelectorAll(".showcase-crop").forEach((img) => (img.hidden = img.dataset.scene !== scene));
    showcase.querySelectorAll(".showcase-panel").forEach((panel) => {
      panel.hidden = panel.dataset.scene !== scene || panel.dataset.lang !== lang;
    });
    showcase.querySelectorAll("[data-pick-scene]").forEach((b) => b.setAttribute("aria-pressed", b.dataset.pickScene === scene));
    showcase.querySelectorAll("[data-pick-lang]").forEach((b) => b.setAttribute("aria-pressed", b.dataset.pickLang === lang));
  };
  showcase.addEventListener("click", (event) => {
    const button = event.target.closest("[data-pick-scene], [data-pick-lang]");
    if (!button) return;
    if (button.dataset.pickScene) showcase.dataset.scene = button.dataset.pickScene;
    if (button.dataset.pickLang) showcase.dataset.lang = button.dataset.pickLang;
    show();
  });
});

const motion = window.matchMedia("(prefers-reduced-motion: reduce)");
const saveData = navigator.connection?.saveData;
document.querySelectorAll("video[data-autoplay]").forEach((video) => {
  const apply = () => {
    const still = motion.matches || saveData;
    video.controls = still;
    if (still) video.pause();
    else video.play().catch(() => {});
  };
  apply();
  motion.addEventListener("change", apply);
});
