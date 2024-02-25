import { PostProps } from "./posts.types";

export interface UserProps {
  badges?: string[];
  bio?: string;
  completedTasks?: string[];
  currentTasks: string[];
  dob?: string;
  email?: string;
  followers?: string[];
  fullName?: string;
  gender?: string;
  location?: string;
  phone?: string;
  points?: number;
  posts?: PostProps[]; // You might want to define a type for posts
  profileComplete?: boolean;
  streak?: number;
  userID: string;
  username?: string;
  cityName: string;
  password?: string;
  countryName: string;
}
