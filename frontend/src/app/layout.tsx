import type { Metadata } from "next";
import "./globals.css";
import { Providers } from "@/components/providers";

export const metadata: Metadata = {
  title: "AiTestOps — Enterprise AI Test Automation SaaS Platform",
  description: "Enterprise SaaS platform for AI-powered test automation, question reasoning, isolated browser practice, and analytics.",
  keywords: ["AI Test Automation", "Spring Boot", "Playwright", "Gemini", "OpenAI", "Next.js"],
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body className="bg-slate-950 text-slate-100 antialiased min-h-screen selection:bg-indigo-500 selection:text-white">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
