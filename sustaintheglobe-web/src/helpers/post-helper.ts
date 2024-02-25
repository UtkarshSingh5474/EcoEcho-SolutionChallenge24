/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable prefer-const */
import {
  getDocs,
  query,
  collection,
  where,
  getDoc,
  doc,
  updateDoc,
  arrayUnion,
  arrayRemove,
  setDoc,
  increment,
  serverTimestamp,
} from "firebase/firestore";
import { db } from "../utils/firebase";
import { PostProps } from "../types/posts.types";
import { toast } from "react-toastify";
import { getDownloadURL, getStorage, ref, uploadBytes } from "firebase/storage";

export const fetchCompletePostList = async (
  queryKey?: string,
  queryValue?: string
) => {
  let postSnapshot;
  if (queryKey && queryValue) {
    postSnapshot = await getDocs(
      query(collection(db, "Posts"), where(queryKey, "==", queryValue))
    );
  } else {
    postSnapshot = await getDocs(collection(db, "Posts"));
  }

  let postDocs: PostProps[] = [];
  postSnapshot.forEach((item) => {
    postDocs.push(item.data() as any);
  });

  let postsWithusers: any[] = [];

  for (let i = 0; i < postDocs.length; i++) {
    const userSnapshot = await getDoc(doc(db, "Users", postDocs[i].userID));
    if (userSnapshot.exists()) {
      const taskSnapshot = await getDoc(
        doc(
          db,
          `Users/${userSnapshot.data().userID}/allUserTasks/${
            postDocs[i].taskID
          }`
        )
      );

      if (taskSnapshot.exists()) {
        const masterTaskSnapshot = await getDoc(
          doc(db, `Tasks/${taskSnapshot.data().masterTaskID}`)
        );
        if (masterTaskSnapshot.exists()) {
          let data = userSnapshot.data();
          let masterTaskData = masterTaskSnapshot.data();
          postsWithusers.push({
            ...postDocs[i],
            user: data,
            category: masterTaskData.category,
          });
        }
      }
    }
  }

  return postsWithusers;
};

export const likeOrDislikePost = async (postId: string, userId: string) => {
  const document = doc(db, "Posts", postId);

  const postSnapshot = await getDoc(document);

  if (postSnapshot.exists()) {
    if (postSnapshot.data().likes.includes(userId)) {
      await updateDoc(document, {
        likes: arrayRemove(userId),
      });
      toast("Removed Like from Post");
    } else {
      await updateDoc(document, {
        likes: arrayUnion(userId),
      });
      toast("Liked Post");
    }
  }
};

export const createPost = async (
  taskId: string,
  postData: any,
  callback: (val?: any) => void
) => {
  const storage = getStorage();
  const file = postData.file;
  const imageRef = ref(storage, `/postMedia/${postData.userId}/${file.name}`);

  uploadBytes(imageRef, file)
    .then((snapshot) => {
      getDownloadURL(snapshot.ref)
        .then(async (imageLink) => {
          const newPostRef = doc(collection(db, "Posts"));
          await setDoc(newPostRef, {
            caption: postData.caption,
            imageLink,
            postTime: serverTimestamp(),
            cityName: postData.cityName,
            countryName: postData.countryName,
            likes: [],
            postID: newPostRef.id,
            taskID: taskId,
            userID: postData.userId,
          });

          await updateDoc(
            doc(db, `Users/${postData.userId}/allUserTasks/${taskId}`),
            {
              postID: newPostRef.id,
            }
          );

          let updatedTask = await getDoc(
            doc(db, `Users/${postData.userId}/allUserTasks/${taskId}`)
          );

          if (updatedTask.exists()) {
            let masterTask = await getDoc(
              doc(db, `Tasks/${updatedTask.data().masterTaskID}`)
            );
            if (masterTask.exists()) {
              let data = masterTask.data();
              const level = data.level;
              await updateDoc(doc(db, `Users/${postData.userId}`), {
                points: increment(level === 1 ? 3 : level === 2 ? 10 : 30),
                completedTasks: arrayUnion(taskId),
              });
            }
          }

          callback(true);
        })
        .catch((err) => {
          toast(err.message ?? "Some Error occurered");
          callback(false);
        });
    })
    .catch((err) => {
      toast(err.message ?? "Some Error occurered");
      callback(false);
    });

  return;
};
