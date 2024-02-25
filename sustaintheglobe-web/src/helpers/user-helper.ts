/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable prefer-const */
import {
  getDocs,
  query,
  collection,
  where,
  getDoc,
  doc,
  orderBy,
  updateDoc,
  arrayRemove,
  arrayUnion,
} from "firebase/firestore";
import { db } from "../utils/firebase";
import { PostProps } from "../types/posts.types";
import { toast } from "react-toastify";
import { UserProps } from "../types/user.types";

export const fetchCompleteUserData = async (userId: string) => {
  if (!userId) return;

  let finalUser: any;

  const userSnapshot = await getDoc(doc(db, "Users", userId));

  if (userSnapshot.exists()) {
    finalUser = userSnapshot.data();
  } else {
    toast("User not found!");
    return;
  }

  const userPostSnapshot = await getDocs(
    query(collection(db, "Posts"), where("userID", "==", userId))
  );
  const userPostDocs: PostProps[] = [];

  let docs = userPostSnapshot.docs;
  for (let i = 0; i < docs.length; i++) {
    const taskRef = await getDoc(
      doc(db, `Users/${userId}/allUserTasks/${docs[i].data().taskID}`)
    );

    if (taskRef.exists()) {
      const masterTaskRef = await getDoc(
        doc(db, `Tasks/${taskRef.data().masterTaskID}`)
      );

      if (masterTaskRef.exists()) {
        userPostDocs.push({
          ...docs[i].data(),
          user: finalUser,
          category: masterTaskRef.data().category,
        } as any);
      }
    }
  }

  finalUser.posts = userPostDocs;

  return finalUser;
};

export const fetchUsersForLeaderboard = async (
  queryKey?: string,
  queryValue?: string,
  followerId?: string
) => {
  let allUsers: UserProps[] = [];

  let usersSnapshot;
  if (queryKey && queryValue) {
    usersSnapshot = await getDocs(
      query(
        collection(db, "Users"),
        where(queryKey, "==", queryValue),
        orderBy("points", "desc")
      )
    );
  } else {
    if (followerId) {
      usersSnapshot = await getDocs(
        query(
          collection(db, "Users"),
          where("followers", "array-contains", followerId),
          orderBy("points", "desc")
        )
      );
    } else {
      usersSnapshot = await getDocs(
        query(collection(db, "Users"), orderBy("points", "desc"))
      );
    }
  }

  usersSnapshot.forEach((item) => {
    allUsers.push(item.data() as any);
  });

  return allUsers;
};

export const followOrUnfollowUser = async (
  thisUserId: string,
  otherUserId: string
) => {
  const thisUserDoc = doc(db, "Users", thisUserId);
  const otherUserDoc = doc(db, "Users", otherUserId);

  const thisUserSnapshot = await getDoc(thisUserDoc);
  const otherUserSnapshot = await getDoc(otherUserDoc);

  if (thisUserSnapshot.exists() && otherUserSnapshot.exists()) {
    if (otherUserSnapshot.data().followers.includes(thisUserId)) {
      await updateDoc(otherUserDoc, {
        followers: arrayRemove(thisUserId),
      });
      await updateDoc(thisUserDoc, {
        following: arrayRemove(otherUserId),
      });
      toast("Unfollowed User");
    } else {
      await updateDoc(otherUserDoc, {
        followers: arrayUnion(thisUserId),
      });
      await updateDoc(thisUserDoc, {
        following: arrayUnion(otherUserId),
      });
      toast("Followed User");
    }
  } else {
    toast("User not found!");
  }
};
