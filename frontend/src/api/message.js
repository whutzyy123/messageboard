import request from '../utils/request'

export const listMessages = (params) => {
  return request({
    url: '/api/messages',
    method: 'get',
    params
  })
}

export const createMessage = (data) => {
  return request({
    url: '/api/messages',
    method: 'post',
    data
  })
}

export const updateMessage = (id, data) => {
  return request({
    url: `/api/messages/${id}`,
    method: 'put',
    data
  })
}

export const deleteMessage = (id) => {
  return request({
    url: `/api/messages/${id}`,
    method: 'delete'
  })
}
