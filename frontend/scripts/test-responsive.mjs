import { chromium } from "playwright";
import fs from "fs";
import path from "path";

const VIEWPORTS = [
  // Mobile
  { name: "mobile-320", width: 320, height: 640 },
  { name: "mobile-360", width: 360, height: 640 },
  { name: "mobile-375", width: 375, height: 667 },
  { name: "mobile-390", width: 390, height: 844 },
  { name: "mobile-414", width: 414, height: 896 },
  { name: "mobile-430", width: 430, height: 932 },
  // Tablet
  { name: "tablet-768", width: 768, height: 1024 },
  { name: "tablet-820", width: 820, height: 1180 },
  { name: "tablet-1024", width: 1024, height: 768 },
  // Laptop
  { name: "laptop-1280", width: 1280, height: 800 },
  { name: "laptop-1366", width: 1366, height: 768 },
  { name: "laptop-1440", width: 1440, height: 900 },
  // Desktop
  { name: "desktop-1600", width: 1600, height: 900 },
  { name: "desktop-1920", width: 1920, height: 1080 },
  // Large Displays
  { name: "large-2560", width: 2560, height: 1440 },
  { name: "large-3840", width: 3840, height: 2160 },
];

const ROUTES = [
  { name: "home", path: "/" },
  { name: "login", path: "/login" },
  { name: "register", path: "/register" },
  { name: "dashboard", path: "/dashboard" },
  { name: "websites", path: "/dashboard/websites" },
  { name: "new-test", path: "/dashboard/tests/new" },
  { name: "tests", path: "/dashboard/tests" },
  { name: "reports", path: "/dashboard/reports" },
  { name: "test-cases", path: "/dashboard/test-cases" },
  { name: "history", path: "/dashboard/history" },
  { name: "team", path: "/dashboard/team" },
  { name: "settings", path: "/dashboard/settings" },
];

const OUTPUT_DIR = path.resolve("..", "artifacts", "responsive");
if (!fs.existsSync(OUTPUT_DIR)) {
  fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

async function run() {
  console.log("Launching Edge/Chromium for Responsive Audit...");
  const browser = await chromium.launch({
    channel: "msedge",
    headless: true,
  });

  const results = [];

  for (const route of ROUTES) {
    console.log(`\nTesting Route: ${route.path} (${route.name})`);
    for (const vp of VIEWPORTS) {
      const context = await browser.newContext({
        viewport: { width: vp.width, height: vp.height },
      });
      const page = await context.newPage();

      try {
        await page.goto(`http://localhost:3000${route.path}`, {
          waitUntil: "networkidle",
          timeout: 15000,
        });

        // Check horizontal overflow
        const overflow = await page.evaluate(() => {
          return document.documentElement.scrollWidth > window.innerWidth;
        });

        // Screenshot for selected key viewports to keep artifact storage clean
        const isKeyViewport = [320, 390, 768, 1280, 1920, 3840].includes(vp.width);
        let screenshotPath = null;
        if (isKeyViewport) {
          screenshotPath = path.join(OUTPUT_DIR, `${route.name}-${vp.width}x${vp.height}.png`);
          await page.screenshot({ path: screenshotPath, fullPage: false });
        }

        const auditItem = {
          route: route.path,
          viewport: `${vp.width}x${vp.height} (${vp.name})`,
          width: vp.width,
          height: vp.height,
          hasHorizontalOverflow: overflow,
          screenshot: screenshotPath ? path.basename(screenshotPath) : null,
          status: overflow ? "FAIL_OVERFLOW" : "PASS",
        };

        results.push(auditItem);
        process.stdout.write(overflow ? "x" : ".");
      } catch (err) {
        console.error(`Error on ${route.path} at ${vp.width}x${vp.height}:`, err.message);
        results.push({
          route: route.path,
          viewport: `${vp.width}x${vp.height}`,
          status: "ERROR",
          error: err.message,
        });
      } finally {
        await context.close();
      }
    }
  }

  await browser.close();

  const summaryFile = path.join(OUTPUT_DIR, "audit-summary.json");
  fs.writeFileSync(summaryFile, JSON.stringify(results, null, 2));

  const total = results.length;
  const passed = results.filter((r) => r.status === "PASS").length;
  const failed = results.filter((r) => r.status !== "PASS").length;

  console.log(`\n\nResponsive Audit Finished!`);
  console.log(`Total tests: ${total}`);
  console.log(`Passed: ${passed}`);
  console.log(`Failed: ${failed}`);
  console.log(`Results saved to: ${summaryFile}`);
}

run().catch((e) => {
  console.error("Audit runner failed:", e);
  process.exit(1);
});
