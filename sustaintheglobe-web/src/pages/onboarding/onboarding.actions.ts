/* eslint-disable @typescript-eslint/no-explicit-any */
import { toast } from "react-toastify";
import {
  collection,
  doc,
  getDoc,
  getDocs,
  query,
  setDoc,
  where,
} from "firebase/firestore";
import { db } from "../../utils/firebase";
import { UserProps } from "../../types/user.types";

export const updateUserInfo = async (
  userId: string,
  userData: UserProps,
  callback: (success: boolean, data?: UserProps) => void
) => {
  const user: UserProps = {
    ...userData,
    profileComplete: true,
  };

  try {
    const querySnapshot = await getDocs(
      query(collection(db, "Users"), where("username", "==", userData.username))
    );

    if (!querySnapshot.empty) {
      toast("Username already exists!");
      callback(false);
    } else {
      setDoc(doc(db, "Users", userId), user, { merge: true })
        .then(() => {
          getDoc(doc(db, "Users", userId)).then((res: any) => {
            if (res.exists()) {
              if (callback) callback(true, res.data());
            }
          });
        })
        .catch((err: any) => {
          toast.success(err.message ?? "Error creating account");
          callback(false);
        });
    }
  } catch (error) {
    toast("Some error occured, try again!");
    callback(false);
  }
};
