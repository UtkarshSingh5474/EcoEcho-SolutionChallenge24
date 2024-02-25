import { CheckIcon } from 'lucide-react'
import { useEffect, useState } from 'react'
import BottomNavbar from '../../components/bottom-nav'
import ic_points from '../../assets/icons/ic_points.svg';
import ic_default_profile from '../../assets/icons/ic_default_profile.svg'
import ic_medal_first from '../../assets/icons/ic_medal_first.svg';
import ic_medal_second from '../../assets/icons/ic_medal_second.svg';
import ic_medal_third from '../../assets/icons/ic_medal_third.svg';
import { useLeaderboardStore } from '../../store/leaderboard.store';
import { useLoaderStore } from '../../store/loader.store';
import { useNavigate } from 'react-router-dom';
import { fetchUsersForLeaderboard } from '../../helpers/user-helper';
import ScreenLoader from '../../components/screen-loader';
import UserLeaderBoardCard from '../../components/user-leaderboard-card';
import { useUserStore } from '../../store/user.store';

const LeaderBoard = () => {
    const [category, setCategory] = useState('Nearby')
    const options = ['Nearby', 'Country', 'World', 'Following'];
    const { leadUsers, saveLeadUsers, removeLeadUsers } = useLeaderboardStore();
    const { enableLoader, disableLoader } = useLoaderStore();
    const navigate = useNavigate()
    const { user } = useUserStore();

    const getData = async () => {
        enableLoader()
        let data;
        if (category === 'Nearby') {
            data = await fetchUsersForLeaderboard('cityName', user?.cityName)
        } else if (category === 'Country') {
            data = await fetchUsersForLeaderboard('countryName', user?.countryName)
        } else if (category === 'World') {
            data = await fetchUsersForLeaderboard()
        } else if (category === 'Following') {
            data = await fetchUsersForLeaderboard('', '', user?.userID)
        }

        if (!data) {
            navigate('/')
        } else {
            saveLeadUsers(data)
        }
        disableLoader()
    }

    useEffect(() => {
        getData()

        return () => {
            removeLeadUsers()
        }
    }, [category])


    if (!leadUsers) {
        return <ScreenLoader />
    }

    return (
        <div className='mx-auto mt-4'>

            <h1 className='text-2xl font-semibold'>Leaderboard</h1>
            <p className='font-semibold text-md text-gray-500'>Top contributors to a sustainable future</p>
            <div className='flex items-center justify-start my-3'>
                {
                    options.map(option => {
                        return <div onClick={() => setCategory(option)} className={`cursor-pointer transition-all flex mr-2 items-center justify-center px-3 py-1 rounded-lg font-semibold text-[10px] border-2  ${category === option ? 'bg-green-500 border-green-500 text-white' : 'text-gray-500 border-gray-300 '}`}>
                            {category === option ? <CheckIcon size={14} className='mr-1' /> : null} {option}
                        </div>
                    })
                }
            </div>


            <div className='flex items-center my-5 justify-evenly w-full '>
                <div onClick={() => {
                    if (!leadUsers[1]) return;
                    navigate(`/profile/${leadUsers[1]?.userID}`)
                }} className={`cursor-pointer self-end text-center ${!leadUsers[1] ? 'opacity-30' : ''}`}>
                    <img src={ic_default_profile} className='h-20 rounded-full w-20 border border-offset-1 border-stone-300' />
                    <div className='flex justify-center'>
                        <img src={ic_medal_second} className='h-6' />
                    </div>
                    <div className="flex justify-center ">
                        <img src={ic_points} className='h-5' />
                        <h4 className='font-semibold text-sm p-0 -mt-[.7px] ml-1'>{leadUsers[1]?.points}</h4>
                    </div>
                    <div className="font-semibold">
                        {leadUsers[1]?.email === user?.email ? 'You' : leadUsers[1]?.fullName ?? '---'}
                    </div>

                </div> 


                <div onClick={() => {
                    if (!leadUsers[0]) return;
                    navigate(`/profile/${leadUsers[0].userID}`)
                }} className={`cursor-pointer self-end text-center ${!leadUsers[0] ? 'opacity-30' : ''}`}>
                    <img src={ic_default_profile} className='h-24 rounded-full border-2 border-offset-1 border-amber-300' />
                    <div className='flex justify-center'>
                        <img src={ic_medal_first} className='h-8' />
                    </div>
                    <div className="flex justify-center ">
                        <img src={ic_points} className='h-5' />
                        <h4 className='font-semibold text-sm p-0 -mt-[.7px] ml-1'>{leadUsers[0]?.points}</h4>
                    </div>
                    <div className="font-semibold">
                        {leadUsers[0]?.email === user?.email ? 'You' : leadUsers[0]?.fullName ?? '---'}
                    </div>
                </div>

                <div onClick={() => {
                    if (!leadUsers[2]) return;
                    navigate(`/profile/${leadUsers[2].userID}`)
                }} className={`cursor-pointer self-end text-center ${!leadUsers[2] ? 'opacity-30' : ''}`}>
                    <img src={ic_default_profile} className='h-20 rounded-full border-2 border-offset-1 border-amber-800' />
                    <div className='flex justify-center'>
                        <img src={ic_medal_third} className='h-6' />
                    </div>
                    <div className="flex justify-center ">
                        <img src={ic_points} className='h-5' />
                        <h4 className='font-semibold text-sm p-0 -mt-[.7px] ml-1'>{leadUsers[2]?.points}</h4>
                    </div>
                    <div className="font-semibold">
                        {leadUsers[2]?.email === user?.email ? 'You' : leadUsers[2]?.fullName ?? '---'}
                    </div>
                </div> 

            </div>

            <div>
                {
                    leadUsers?.length > 3 ? leadUsers?.slice(3).map((item, index: number) => {
                        return <UserLeaderBoardCard rank={index + 4} data={item} />
                    }) : null
                }
            </div>



            <BottomNavbar />
        </div>
    )
}

export default LeaderBoard