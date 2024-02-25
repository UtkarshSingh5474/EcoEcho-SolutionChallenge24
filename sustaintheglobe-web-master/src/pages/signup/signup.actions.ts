/* eslint-disable @typescript-eslint/no-explicit-any */
import { getAuth } from "firebase/auth";
import { createUserWithEmailAndPassword } from "firebase/auth";
import { toast } from "react-toastify";
import { doc, setDoc } from "firebase/firestore";
import { db } from "../../utils/firebase";
import { UserProps } from "../../types/user.types";
import moment from "moment";

export const createUser = (userData: any, callback: (_id?: string) => void) => {
  const auth = getAuth();
  createUserWithEmailAndPassword(auth, userData.email, userData.password)
    .then(async (userCredential: any) => {
      const docId = userCredential.user.uid;
      const userDto: UserProps = {
        ...userData,
        userID: docId,
        currentTasks: [],
        phone: userData.phone,
        dob: moment(userData.dob).format("DD/MM/YYYY"),
        completedTasks: [],
        points: 0,
        posts: [],
        followers: [],
        badges: [],
        gender: [],
        location: null,
        streak: 0,
        username: "",
        profileComplete: false,
      };
      delete userDto.password;
      setDoc(doc(db, "Users", docId), userDto)
        .then(() => {
          if (callback) callback(docId);
        })
        .catch((err: any) => {
          toast.success(err.message ?? "Error creating account");
          callback();
        });
    })
    .catch((error) => {
      const errorMessage = error.message;
      toast(errorMessage);
      callback();
    });
};
