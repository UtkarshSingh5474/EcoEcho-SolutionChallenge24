/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable prefer-const */
import {
  getAuth,
  sendPasswordResetEmail,
  signInWithEmailAndPassword,
} from "firebase/auth";
import { toast } from "react-toastify";
import { UserProps } from "../../types/user.types";
import { getDoc, doc } from "firebase/firestore";
import { db } from "../../utils/firebase";

export const loginUser = (
  email: string,
  password: string,
  callback: (user?: UserProps) => void
) => {
  const auth = getAuth();
  signInWithEmailAndPassword(auth, email, password)
    .then(async (userCredential) => {
      const user = userCredential.user;
      const querySnapshot = await getDoc(doc(db, "Users", user.uid));

      if (querySnapshot.exists()) {
        if (callback) callback(querySnapshot.data() as any);
      } else {
        callback();
      }
    })
    .catch((error) => {
      const errorMessage = error.message;
      toast(errorMessage);
      callback();
    });
};

export const forgotPassword = async (email: string) => {
  try {
    const auth = getAuth();

    await sendPasswordResetEmail(auth, email);

    toast("Please check your inbox for mail!");
  } catch (err: any) {
    toast(err?.message ?? "Some error occured while forgetting password");
  }
};