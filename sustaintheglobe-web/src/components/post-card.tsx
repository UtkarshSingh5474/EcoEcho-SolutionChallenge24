import { Clock3Icon, HeartIcon, UserCircle2 } from "lucide-react"
import { COLORS } from "../utils/colors"
import moment from "moment"
import { PostProps } from "../types/posts.types"
import { UserProps } from "../types/user.types"

const weeks = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
const PostCard = ({ postData, userData, likeHandler }: { postData: PostProps, userData?: UserProps, likeHandler: () => void }) => {
    const { imageLink, likes, caption, user, postTime } = postData
    return (
        <div className="relative p-2 rounded-xl bg-white border-2 my-3 w-full">
            <img src={imageLink} width={'100%'} height={'300px'} className="rounded-xl h-[250px] object-cover" />
            <div onClick={() => likeHandler()} className="w-[50px] bg-green-100 cursor-pointer p-2 border-2 text-center rounded-lg absolute top-5 right-5">
                <HeartIcon color={COLORS.primary} className="mx-auto" size={25} />
                <p className="text-center text-green-500 font-semibold">{likes.length}</p>
            </div>
            <div className="py-2">
                <h2 className="text-xl font-semibold">{postData.category}</h2>
                <p className="text-sm text-gray-400">Caption: {caption}</p>
            </div>
            <div className="flex items-center justify-between">
                <div className="flex items-center bg-gray-100 p-1 rounded-xl px-2 justify-start">
                    <UserCircle2 size={18} color={COLORS.primary} />
                    <p className="text-xs ml-1 -mt-1">@{userData?.username ?? user?.username}</p>
                </div>
                <div className="flex items-center bg-gray-100 p-1 rounded-xl px-2 justify-start">
                    <Clock3Icon size={18} color={COLORS.primary} />
                    <p className="text-xs ml-1 -mt-1">
                        {weeks[moment(postTime?.seconds * 1000).isoWeekday() - 1]} {moment(postTime?.seconds * 1000).format('DD MMM, YYYY')}
                    </p>
                </div>
            </div>
        </div>
    )
}

export default PostCard