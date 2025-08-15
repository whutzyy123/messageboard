import request from '../utils/request'

/**
 * 用户点赞留言
 * 
 * @param {number} messageId 留言ID
 * @returns {Promise} API响应
 */
export const likeMessage = (messageId) => {
  return request({
    url: `/api/messages/${messageId}/like`,
    method: 'post'
  })
}

/**
 * 用户取消点赞
 * 
 * @param {number} messageId 留言ID
 * @returns {Promise} API响应
 */
export const unlikeMessage = (messageId) => {
  return request({
    url: `/api/messages/${messageId}/like`,
    method: 'delete'
  })
}

/**
 * 检查用户是否已点赞某条留言
 * 
 * @param {number} messageId 留言ID
 * @returns {Promise} API响应
 */
export const getLikeStatus = (messageId) => {
  return request({
    url: `/api/messages/${messageId}/like/status`,
    method: 'get'
  })
}

/**
 * 获取留言的点赞数量
 * 
 * @param {number} messageId 留言ID
 * @returns {Promise} API响应
 */
export const getLikeCount = (messageId) => {
  return request({
    url: `/api/messages/${messageId}/like/count`,
    method: 'get'
  })
}

/**
 * 获取热门留言列表
 * 
 * @param {Object} params 查询参数
 * @returns {Promise} API响应
 */
export const getHotMessages = (params) => {
  return request({
    url: '/api/messages/hot',
    method: 'get',
    params
  })
}

/**
 * 获取所有热门留言
 * 
 * @returns {Promise} API响应
 */
export const getAllHotMessages = () => {
  return request({
    url: '/api/messages/hot/all',
    method: 'get'
  })
}
