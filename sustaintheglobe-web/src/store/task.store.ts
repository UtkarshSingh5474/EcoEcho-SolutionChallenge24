import { create } from "zustand";
import { TaskProps } from "../types/task.types";

export type TaskStoreProps = {
  tasks: TaskProps[] | null;
  saveTasks: (tasks: TaskProps[]) => void;
  removeTasks: () => void;
};
export const useTaskStore = create<TaskStoreProps>((set) => ({
  tasks: null,
  saveTasks: (tasks: TaskProps[]) => set(() => ({ tasks: tasks })),
  removeTasks: () => set({ tasks: null }),
}));
