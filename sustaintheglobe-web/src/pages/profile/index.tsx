/* eslint-disable prefer-const */
/* eslint-disable @typescript-eslint/no-explicit-any */
import profileIcon from '../../assets/icons/ic_default_profile.svg'
import PostCard from '../../components/post-card'
import NoPosts from '../../components/no-posts'
import { ArrowLeft, LogOutIcon, UserPlus } from 'lucide-react'
import { useUserStore } from '../../store/user.store'
import BottomNavbar from '../../components/bottom-nav'
import { useNavigate, useParams } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { UserProps } from '../../types/user.types'
import { useLoaderStore } from '../../store/loader.store'
import { fetchCompleteUserData, followOrUnfollowUser } from '../../helpers/user-helper'
import { likeOrDislikePost } from '../../helpers/post-helper'
import ScreenLoader from '../../components/screen-loader'
import { toast } from 'react-toastify'

const ProfilePage = () => {
    const { userId } = useParams();
    let [profileUser, setProfileUser] = useState<UserProps | undefined>()
    const { enableLoader, disableLoader } = useLoaderStore()
    const { user, removeUser } = useUserStore();
    const navigate = useNavigate();

    const getUserAction = async () => {
        enableLoader()
        if (userId) {
            const data = await fetchCompleteUserData(userId);

            if (!data) {
                navigate('/')
                disableLoader()
                return;
            }
            setProfileUser(data as any)
        }
        disableLoader()
    }


    useEffect(() => {
        getUserAction()
    }, [])

    const handleFollowUser = async () => {
        if (user?.userID && profileUser?.userID) {
            await followOrUnfollowUser(user?.userID, profileUser?.userID)
            getUserAction();
        } else {
            removeUser();
            navigate('/')
        }
    }


    if (!profileUser) {
        return <ScreenLoader />
    }

    return (
        <div className='flex items-center flex-col justify-top my-auto h-screen '>
            <div className="relative pt-[60px] w-full flex flex-col items-center min-h-[400px]">
                <div className='fixed top-0 lef-0 md:left-[37.5%] w-full md:w-1/4 bg-green-500 mx-auto p-1 flex items-center justify-between '>
                    <div className='cursor-pointer' onClick={() => {
                        navigate(-1)
                    }}>
                        <ArrowLeft className='text-white ml-1' />
                    </div>
                    {user?.userID === profileUser?.userID ? <div onClick={() => {
                        removeUser()
                        navigate('/', {
                            replace: true
                        })
                        toast('Logged out!')
                    }} className='cursor-pointer flex items-center w-fit justify-center border-[1.2px] border-gray-300 p-2 rounded-lg bg-white '>
                        <p className='font-semibold text-sm text-green-500'>Logout</p>
                        <LogOutIcon className='text-green-500 ml-1' size={18} />
                    </div> : <div onClick={() => handleFollowUser()} className='cursor-pointer flex items-center w-fit justify-center border-[1.2px] border-gray-300 p-2 rounded-lg bg-white '>
                            <p className='font-semibold text-sm text-green-500'>{user?.userID && profileUser.followers?.includes(user?.userID) ? 'Unfollow' : 'Follow'}</p>
                        <UserPlus className='text-green-500 ml-1' size={18} />
                    </div>}
                </div>
                <div className='flex py-2 px-3 w-full items-center justify-start'>
                    <img src={profileIcon} width={100} height={100} />
                    <div className='w-full ml-2'>
                        <h2 className='text-lg font-semibold -mb-1'>{profileUser?.fullName}</h2>
                        <h4 className='font-semibold text-md text-gray-400 my-1'>@{profileUser?.username}</h4>
                        <p className='w-fit px-2 bg-gray-200 rounded-xl py-1 text-sm'>
                            {profileUser?.bio?.substring(0, 50)}
                        </p>
                    </div>
                </div>
                <div className='flex w-full items-center justify-around'>
                    <div className='w-1/3 text-center'>
                        <h1 className='text-xl font-semibold text-black'>{profileUser?.points}</h1>
                        <p className='text-gray-400'>Points</p>
                    </div>
                    <div className='w-1/3 text-center'>
                        <h1 className='text-xl font-semibold text-black'>{profileUser?.completedTasks?.length}</h1>
                        <p className='text-gray-400'>Task Done</p>
                    </div>
                    <div className='w-1/3 text-center'>
                        <h1 className='text-xl font-semibold text-black'>{profileUser?.followers?.length}</h1>
                        <p className='text-gray-400'>Followers</p>
                    </div>
                </div>
                <div className='pb-[100px] w-full'>
                    {
                        profileUser?.posts?.length ? profileUser?.posts.map(item => {
                            return <PostCard likeHandler={async () => {
                                if (user?.userID) {
                                    await likeOrDislikePost(item.postID, user.userID)
                                    getUserAction()
                                } else {
                                    navigate('/')
                                }
                            }} postData={item} userData={profileUser} />
                        }) : <NoPosts />
                    }
                </div>
            </div>
            <BottomNavbar />
        </div>
    )
}

export default ProfilePage