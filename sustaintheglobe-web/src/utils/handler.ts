import { TaskProps } from "../types/task.types";

export const getDailyTasks = (tasks: TaskProps[]) => {
  return tasks.filter((task: TaskProps) => {
    return task.level === 1;
  });
};

export const getWeeklyTasks = (tasks: TaskProps[]) => {
  return tasks.filter((task: TaskProps) => {
    return task.level === 2;
  });
};
export const getMonthlyTasks = (tasks: TaskProps[]) => {
  return tasks.filter((task: TaskProps) => {
    return task.level === 3;
  });
};


export const formatDate = (dateStr: string) => {
  const dt = dateStr.split("/");
  return `${dt[2]}-${dt[1]}-${dt[0]}`;
};