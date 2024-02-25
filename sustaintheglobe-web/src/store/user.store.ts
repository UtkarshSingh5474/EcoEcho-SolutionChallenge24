import { create } from "zustand";
import { UserProps } from "../types/user.types";

export type UserStoreProps = {
  user: UserProps | null;
  saveUser: (userData: UserProps) => void;
  removeUser: () => void;
};
export const useUserStore = create<UserStoreProps>((set) => ({
  user: null,
  saveUser: (userData: UserProps) => set(() => ({ user: userData })),
  removeUser: () => set({ user: null }),
}));
