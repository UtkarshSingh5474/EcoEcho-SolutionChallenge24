import {
    createBrowserRouter,
    RouterProvider,
} from "react-router-dom";
import LoginPage from "../login";
import SignupPage from "../signup";
import { ToastContainer } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import OnboardingPage from "../onboarding";
import ProfilePage from "../profile";
import { initFirebaseApp } from "../../utils/firebase";
import FeedPage from "../feed";
import LeaderBoard from "../leaderboard"
import TaskPage from "../tasks";
import AddPost from "../add-post"


import ActionLoader from "../../components/action-loader";
import { useLoaderStore } from "../../store/loader.store";

const router = createBrowserRouter([
    {
        path: "/",
        element: <LoginPage />,
    },
    {
        path: "/signup",
        element: <SignupPage />,
    },
    {
        path: "/:userId/onboarding",
        element: <OnboardingPage />,
    },
    {
        path: "/profile/:userId",
        element: <ProfilePage />,
    },
    {
        path: "/feed",
        element: <FeedPage />,
    },
    {
        path: "/leaderboard",
        element: <LeaderBoard />,
    },
    {
        path: "/tasks",
        element: <TaskPage />,
    },
    
    {
        path: "/:taskId/addpost",
        element: <AddPost />,
    },

]);
const Router = () => {
    const { loader } = useLoaderStore();
    initFirebaseApp()
    return (
        <div className="w-11/12 md:w-1/4 mx-auto">
            <ToastContainer />
            {loader ? <ActionLoader /> : null}
            <RouterProvider router={router} />
        </div>
    )
}

export default Router