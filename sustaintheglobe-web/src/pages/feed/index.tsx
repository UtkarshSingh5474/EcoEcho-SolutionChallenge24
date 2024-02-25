import { CheckIcon } from 'lucide-react'
import { useEffect, useState } from 'react'
import { usePostStore } from '../../store/feed.store'
import NoPosts from '../../components/no-posts'
import PostCard from '../../components/post-card'
import BottomNavbar from '../../components/bottom-nav'
import { fetchCompletePostList, likeOrDislikePost } from '../../helpers/post-helper'
import { useUserStore } from '../../store/user.store'
import { useLoaderStore } from '../../store/loader.store'
import { useNavigate } from 'react-router-dom'

const FeedPage = () => {
    const [category, setCategory] = useState('Nearby')
    const { user } = useUserStore();
    const { enableLoader, disableLoader } = useLoaderStore();
    const { posts, savePosts, removePosts } = usePostStore();
    const options = ['Nearby', 'Country', 'World'];
    const navigate = useNavigate()

    const getData = async () => {
        enableLoader()
        let data;
        if (category === 'Nearby') {
            data = await fetchCompletePostList('cityName', user?.cityName)
        } else if (category === 'Country') {
            data = await fetchCompletePostList('countryName', user?.countryName)
        } else if (category === 'World') {
            data = await fetchCompletePostList()
        } else return;

        if (!data) {
            navigate('/')
        } else {
            savePosts(data)
        }
        disableLoader()
    }

    useEffect(() => {
        getData()

        return () => {
            removePosts()
        }
    }, [category])
    return (
        <div className='mt-4'>
            <h1 className='text-2xl font-semibold'>Feed</h1>
            <p className='font-semibold text-gray-500'>See what others have Done!</p>

            <div className='flex items-center justify-start my-3'>
                {
                    options.map(option => {
                        return <div onClick={() => setCategory(option)} className={`cursor-pointer transition-all flex mr-2 items-center justify-center px-4 py-1 rounded-lg font-semibold text-xs border-2  ${category === option ? 'bg-green-500 border-green-500 text-white' : 'text-gray-500 border-gray-300 '}`}>
                            {category === option ? <CheckIcon size={14} className='mr-1' /> : null} {option}
                        </div>
                    })
                }
            </div>

            <div className='pb-[100px]'>
                {
                    posts?.length ? posts.map(item => {
                        return <PostCard likeHandler={async () => {
                            if (user?.userID) {
                                await likeOrDislikePost(item.postID, user.userID)
                                getData()
                            } else {
                                navigate('/')
                            }
                        }} postData={item} />
                    }) : <NoPosts />
                }
            </div>
            <BottomNavbar />
        </div>
    )
}

export default FeedPage