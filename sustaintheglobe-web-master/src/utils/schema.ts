export type UserSchema = {
  bio?: string;
  currentTasks?: string[];
  dob?: string;
  email?: string;
  fullName?: string;
  gender?: string;
  location?: string;
  phone?: string;
  country?: string;
  city?: string;
  points?: number;
  profileComplete?: boolean;
  streak?: number;
  userID?: string;
  username?: string;
};

export type PostSchema = {
  caption: string;
  imageLink: string;
  countryName: string;
  cityName: string;
  postId: string;
  postTime: string;
  taskId: string;
  userId: string;
  username: string;
};

export type TaskSchema = {
  category: string;
  title: string;
  level: string;
  taskId: string;
  desc: string;
};

export type LikesSchema = {
  postId: string;
  userId: string;
  createdAt: string; //optional;
};

export type FollowerSchema = {
  user1Id: string;
  user2Id: string;
  createdAt: string; //optional
};
