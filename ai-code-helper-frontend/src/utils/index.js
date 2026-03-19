/**
 * 生成聊天室ID
 * @returns {number} 适合int范围的聊天室ID
 */
export function generateMemoryId() {
    // 使用当前时间戳的后9位，确保在int范围内
    return Math.floor(Date.now() % 1000000000)
}

/**
 * 格式化时间
 * @param {Date|string} date 日期对象或时间字符串
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(date) {
    // 如果是字符串，转换为Date对象
    if (typeof date === 'string') {
        date = new Date(date)
    }
    
    // 确保是有效的Date对象
    if (!(date instanceof Date) || isNaN(date.getTime())) {
        return '刚刚'
    }
    
    const now = new Date()
    const diff = now - date
    
    if (diff < 60000) { // 1分钟内
        return '刚刚'
    } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`
    } else if (diff < 86400000) { // 1天内
        return `${Math.floor(diff / 3600000)}小时前`
    } else {
        return date.toLocaleDateString()
    }
}

/**
 * 防抖函数
 * @param {Function} func 要防抖的函数
 * @param {number} wait 等待时间
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, wait) {
    let timeout
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout)
            func(...args)
        }
        clearTimeout(timeout)
        timeout = setTimeout(later, wait)
    }
} 