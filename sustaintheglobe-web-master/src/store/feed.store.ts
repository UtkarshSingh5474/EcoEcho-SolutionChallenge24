import { create } from "zustand";
import { PostProps } from "../types/posts.types";

export type PostStoreProps = {
  posts: PostProps[] | null;
  savePosts: (posts: PostProps[]) => void;
  removePosts: () => void;
};
export const usePostStore = create<PostStoreProps>((set) => ({
  posts: null,
  savePosts: (posts: PostProps[]) => set(() => ({ posts: posts })),
  removePosts: () => set({ posts: null }),
}));
