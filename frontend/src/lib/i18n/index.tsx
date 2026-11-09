"use client";

import React, { createContext, useContext, useState, useEffect } from "react";
import { SupportedLocale, SUPPORTED_LOCALES, Translations, LocaleMeta } from "./types";
import { en } from "./locales/en";
import { uzLatn } from "./locales/uz-Latn";
import { uzCyrl } from "./locales/uz-Cyrl";
import { ru } from "./locales/ru";

const translationsMap: Record<SupportedLocale, Translations> = {
  en,
  "uz-Latn": uzLatn,
  "uz-Cyrl": uzCyrl,
  ru,
};

interface I18nContextType {
  locale: SupportedLocale;
  setLocale: (locale: SupportedLocale) => void;
  t: Translations;
  supportedLocales: LocaleMeta[];
}

const I18nContext = createContext<I18nContextType>({
  locale: "en",
  setLocale: () => {},
  t: en,
  supportedLocales: SUPPORTED_LOCALES,
});

const STORAGE_KEY = "aitestops_locale";

export function I18nProvider({ children }: { children: React.ReactNode }) {
  const [locale, setLocaleState] = useState<SupportedLocale>("en");

  useEffect(() => {
    if (typeof window !== "undefined") {
      const saved = localStorage.getItem(STORAGE_KEY) as SupportedLocale | null;
      if (saved && (saved === "en" || saved === "uz-Latn" || saved === "uz-Cyrl" || saved === "ru")) {
        setLocaleState(saved);
      }
    }
  }, []);

  const setLocale = (newLocale: SupportedLocale) => {
    setLocaleState(newLocale);
    if (typeof window !== "undefined") {
      localStorage.setItem(STORAGE_KEY, newLocale);
      document.documentElement.lang = newLocale;
    }
  };

  const t = translationsMap[locale] || en;

  return (
    <I18nContext.Provider value={{ locale, setLocale, t, supportedLocales: SUPPORTED_LOCALES }}>
      {children}
    </I18nContext.Provider>
  );
}

export function useI18n() {
  return useContext(I18nContext);
}
