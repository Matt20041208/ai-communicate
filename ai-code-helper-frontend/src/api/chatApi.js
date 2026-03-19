import axios from 'axios'

// 配置axios基础URL
const API_BASE_URL = 'http://localhost:53705/api'

/**
 * 获取存储的JWT Token
 * @returns {string|null} JWT Token
 */
function getToken() {
  try {
    const token = localStorage.getItem('token')
    // 确保返回字符串类型
    if (token && typeof token === 'string') {
      return token
    }
    // 清除无效的token
    localStorage.removeItem('token')
    return null
  } catch (error) {
    console.error('获取token失败:', error)
    return null
  }
}

/**
 * 使用 SSE 方式调用聊天接口
 * @param {number} memoryId 聊天室ID
 * @param {string} message 用户消息
 * @param {Function} onMessage 接收消息的回调函数
 * @param {Function} onError 错误处理回调函数
 * @param {Function} onClose 连接关闭回调函数
 * @returns {EventSource} 返回 EventSource 对象，用于手动关闭连接
 */
export function chatWithSSE(memoryId, message, onMessage, onError, onClose) {
    // 获取Token
    const token = getToken()
    
    // 构建URL参数
    const params = new URLSearchParams({
        memoryId: memoryId,
        message: message
    })
    
    // 创建 EventSource 连接，添加Token到URL
    let url = `${API_BASE_URL}/ai/chat?${params}`
    if (token) {
        url += `&token=${encodeURIComponent(token)}`
    }
    
    console.log('SSE连接URL:', url.substring(0, 100) + '...')
    
    const eventSource = new EventSource(url)
    let hasReceivedMessage = false
    let isConnectionClosed = false
    
    // 处理接收到的消息
    eventSource.onmessage = function(event) {
        try {
            hasReceivedMessage = true
            const data = event.data
            console.log('收到SSE消息:', data.substring(0, 50) + '...')
            if (data && data.trim() !== '') {
                onMessage(data)
            }
        } catch (error) {
            console.error('解析消息失败:', error)
            onError && onError(error)
        }
    }
    
    // 处理错误
    eventSource.onerror = function(error) {
        console.log('SSE 连接状态:', eventSource.readyState)
        
        // 如果已经收到过消息，可能是正常结束
        if (hasReceivedMessage && eventSource.readyState === EventSource.CLOSED) {
            console.log('SSE 连接正常结束（已收到消息）')
            isConnectionClosed = true
            onClose && onClose()
            return
        }
        
        // 如果连接已关闭，不重复处理
        if (isConnectionClosed) {
            return
        }
        
        // 只有在连接状态不是正常关闭时才报错
        if (eventSource.readyState === EventSource.CLOSED) {
            console.log('SSE 连接已关闭')
            isConnectionClosed = true
            onClose && onClose()
        } else {
            console.error('SSE 连接错误:', error)
            // 延迟报告错误，给连接恢复的机会
            setTimeout(() => {
                if (!hasReceivedMessage && !isConnectionClosed) {
                    onError && onError(error)
                }
            }, 1000)
        }
        
        // 确保连接关闭
        if (eventSource.readyState !== EventSource.CLOSED) {
            eventSource.close()
        }
    }
    
    // 处理连接打开
    eventSource.onopen = function() {
        console.log('SSE 连接已打开')
    }
    
    // 处理连接关闭
    eventSource.onclose = function() {
        console.log('SSE 连接已关闭')
        isConnectionClosed = true
        onClose && onClose()
    }
    
    return eventSource
}
