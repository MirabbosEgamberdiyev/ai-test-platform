"use client";

import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useI18n } from "@/lib/i18n";
import { Users2, Shield, UserPlus } from "lucide-react";

export default function TeamPage() {
  const { t } = useI18n();

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">{t.nav.team}</h1>
          <p className="text-sm text-slate-400 mt-1">
            Manage organization members, role-based access control (RBAC), and team testing workspaces.
          </p>
        </div>

        <Button className="bg-indigo-600 hover:bg-indigo-500 text-white">
          <UserPlus className="mr-2 h-4 w-4" />
          Invite Team Member
        </Button>
      </div>

      <Card>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-300">
              <thead className="bg-slate-900/60 text-slate-400 uppercase text-[10px] tracking-wider border-b border-slate-800">
                <tr>
                  <th className="py-3.5 px-4">Member Name</th>
                  <th className="py-3.5 px-4">Email</th>
                  <th className="py-3.5 px-4">Role</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                <tr className="hover:bg-slate-900/40">
                  <td className="py-3.5 px-4 font-semibold text-white">Admin User</td>
                  <td className="py-3.5 px-4 font-mono text-slate-400">admin@example.com</td>
                  <td className="py-3.5 px-4">
                    <Badge variant="default" className="text-[10px]">TENANT_ADMIN</Badge>
                  </td>
                  <td className="py-3.5 px-4">
                    <span className="text-emerald-400 text-xs">Active</span>
                  </td>
                  <td className="py-3.5 px-4 text-right">
                    <Button variant="ghost" size="sm" className="h-7 text-xs text-slate-400">
                      Manage
                    </Button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
