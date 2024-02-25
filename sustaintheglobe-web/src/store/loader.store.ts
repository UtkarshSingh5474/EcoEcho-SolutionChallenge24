import { create } from "zustand";

export type LoaderStoreProps = {
  loader: boolean;
  enableLoader: () => void;
  disableLoader: () => void;
};
export const useLoaderStore = create<LoaderStoreProps>((set) => ({
  loader: false,
  enableLoader: () => set({ loader: true }),
  disableLoader: () => set({ loader: false }),
}));
