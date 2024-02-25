import { create } from "zustand";
import { UserProps } from "../types/user.types";

export type LeaderBoardProps = {
  leadUsers: UserProps[] | null;
  saveLeadUsers: (leadUsers: UserProps[]) => void;
  removeLeadUsers: () => void;
};
export const useLeaderboardStore = create<LeaderBoardProps>((set) => ({
  leadUsers: null,
  saveLeadUsers: (leadUsers: UserProps[]) =>
    set(() => ({ leadUsers: leadUsers })),
  removeLeadUsers: () => set({ leadUsers: null }),
}));
