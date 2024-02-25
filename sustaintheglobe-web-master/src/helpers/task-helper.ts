/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  getDoc,
  doc,
  getDocs,
  collection,
  query,
  where,
  serverTimestamp,
  setDoc,
  updateDoc,
} from "firebase/firestore";
import { toast } from "react-toastify";
import { db } from "../utils/firebase";
import moment from "moment";

export const getUserCurrentTasks = async (_userId: string) => {
  const data = await getDoc(doc(db, "Users/" + _userId));

  let taskArray: any = [];

  if (data.exists()) {
    taskArray = data.data().currentTasks;
  } else {
    toast("User not found!");
    return;
  }

  for (let i = 0; i < taskArray.length; i++) {
    const userTaskSnapshot = await getDoc(
      doc(db, `Users/${_userId}/allUserTasks/${taskArray[i]}`)
    );

    if (userTaskSnapshot.exists()) {
      const taskSnapshot = await getDoc(
        doc(db, "Tasks", userTaskSnapshot.data()?.masterTaskID)
      );
      if (taskSnapshot.exists()) {
        taskArray[i] = {
          ...taskSnapshot.data(),
          ...userTaskSnapshot.data(),
        };
      }
    }
  }

  return taskArray;
};

export const createNewTasksForNewUser = async (
  _userId: string,
  booleanArray: boolean[],
  oldTaskIds: string[]
) => {
  const newTaskIds: string[] = [...oldTaskIds];
  for (let i = 0; i < booleanArray.length; i++) {
    if (booleanArray[i]) {
      const level = i < 2 ? 1 : i < 3 ? 2 : 3;

      const masterTasksByLevel = await getDocs(
        query(collection(db, "Tasks"), where("level", "==", level))
      );

      if (!masterTasksByLevel.empty && masterTasksByLevel.docs) {
        let randomMasterTask =
          masterTasksByLevel.docs[
            Math.floor(Math.random() * masterTasksByLevel.docs.length)
          ];

        if (newTaskIds.includes(randomMasterTask.id)) {
          randomMasterTask =
            masterTasksByLevel.docs[
              Math.floor(Math.random() * masterTasksByLevel.docs.length)
            ];
        }

        if (newTaskIds.includes(randomMasterTask.id)) {
          randomMasterTask =
            masterTasksByLevel.docs[
              Math.floor(Math.random() * masterTasksByLevel.docs.length)
            ];
        }
        const newChildTaskRef = doc(
          collection(db, "Users", _userId, "allUserTasks")
        );
        const newChildTask = {
          created: serverTimestamp(),
          masterTaskID: randomMasterTask.id,
          postID: "",
          taskID: newChildTaskRef.id,
          personalised: false,
        };

        try {
          await setDoc(newChildTaskRef, newChildTask);
          newTaskIds[i] = newChildTaskRef.id;
        } catch (err) {
          console.log(newChildTask, "failed to put on firestore");
          return;
        }
      } else {
        toast("No master tasks found!");
        return;
      }
    }
  }

  const thisUserRef = doc(db, "Users", _userId);
  await updateDoc(thisUserRef, {
    currentTasks: newTaskIds,
  });

  toast(
    `Yayy! you got ${booleanArray.filter((item) => item).length} new tasks`
  );

  return getUserCurrentTasks(_userId);
};

export const getOrCreateUserTasks = async (
  _userId: string,
  currentTaskIds: string[]
) => {
  const userCurrentTasks = await getUserCurrentTasks(_userId);

  if (!userCurrentTasks.length) {
    console.log("length of current tasks was 0, I created 4");
    const newTasksForUser = await createNewTasksForNewUser(
      _userId,
      [true, true, true, true],
      currentTaskIds
    );

    return newTasksForUser;
  }

  const lastExecTimestamp: any = localStorage.getItem("REPLACED_EXPIRED_TASKS");
  if (
    !lastExecTimestamp ||
    moment(JSON.parse(lastExecTimestamp).time).isBefore(
      moment().format("YYYY-MM-DD")
    )
  ) {
    const expiracyArray = [false, false, false, false];
    let expiredCount = 0;
    for (let i = 0; i < expiracyArray.length; i++) {
      if (
        checkIfTaskExpired(
          userCurrentTasks[i].created,
          userCurrentTasks[i].level
        )
      ) {
        expiracyArray[i] = true;
        expiredCount++;
      }
    }

    if (expiredCount === 0) {
      console.log("no task expired, so I returned old ones");
      localStorage.setItem(
        "REPLACED_EXPIRED_TASKS",
        JSON.stringify({
          time: Date.now(),
        })
      );
      return userCurrentTasks;
    } else {
      console.log("these tasks expired", expiracyArray);
      const newTasksForUser = await createNewTasksForNewUser(
        _userId,
        expiracyArray,
        currentTaskIds
      );
      localStorage.setItem(
        "REPLACED_EXPIRED_TASKS",
        JSON.stringify({
          time: Date.now(),
        })
      );
      return newTasksForUser;
    }
  } else {
    console.log("already replaced for 1 time, so returning as it is");
    return userCurrentTasks;
  }
};

const checkIfTaskExpired = (timestamp: any, level: number) => {
  const now = moment();
  const taskDate = moment(timestamp?.seconds * 1000);

  const daysDiff = now.diff(taskDate, "days");
  const weeksDiff = now.diff(taskDate, "weeks");
  const monthsDiff = now.diff(taskDate, "months");

  switch (level) {
    case 1:
      return daysDiff >= 1;
    case 2:
      return weeksDiff >= 1;
    case 3:
      return monthsDiff >= 1;
    default:
      return false;
  }
};
