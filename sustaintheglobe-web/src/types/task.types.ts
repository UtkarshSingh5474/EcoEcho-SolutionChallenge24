export type TaskProps = {
  image: string;
  title: string;
  score: string;
  desc: string;
  category: string;
  postID?: string;
  taskID: string;
  level: 1 | 2 | 3;
};
