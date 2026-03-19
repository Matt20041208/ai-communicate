<template>
  <!-- 主界面 - 直接使用AI问答功能 -->
  <div class="app">
    <!-- 头部标题 -->
    <div class="app-header">
      <h1 class="app-title">AI 编程小助手</h1>
      <div class="app-subtitle">帮助您解答编程学习和求职面试相关问题</div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-container">
      <!-- 消息列表 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-content">
            <div class="welcome-icon">🤖</div>
            <h2>欢迎使用 AI 编程小助手</h2>
            <p>我可以帮助您：</p>
            <ul>
              <li>解答编程技术问题</li>
              <li>提供代码示例和解释</li>
              <li>协助求职面试准备</li>
              <li>分享编程学习建议</li>
            </ul>
            <p>请随时向我提问吧！</p>
          </div>
        </div>

        <!-- 历史消息 -->
        <ChatMessage
          v-for="message in messages"
          :key="message.id"
          :message="message.content"
          :is-user="message.isUser"
          :timestamp="message.timestamp"
        />

        <!-- AI 正在回复的消息 -->
        <div v-if="isAiTyping" class="chat-message ai-message">
          <div class="message-avatar">
            <div class="avatar ai-avatar">AI</div>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="ai-typing-content">
                <div class="ai-response-text message-markdown" v-html="currentAiResponseRendered"></div>
                <LoadingDots v-if="isStreaming" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <ChatInput
        :disabled="isAiTyping"
        @send-message="sendMessage"
        placeholder="请输入您的编程问题..."
      />
    </div>

    <!-- 连接状态提示 -->
    <div v-if="connectionError" class="connection-error">
      <div class="error-content">
        <span class="error-icon">⚠️</span>
        <span>连接服务器失败，请检查后端服务是否启动</span>
      </div>
    </div>
  </div>
</template>

<script>
import ChatMessage from './components/ChatMessage.vue'
import ChatInput from './components/ChatInput.vue'
import LoadingDots from './components/LoadingDots.vue'
import { chatWithSSE } from './api/chatApi.js'
import { generateMemoryId } from './utils/index.js'
import { marked } from 'marked'

export default {
  name: 'App',
  components: {
    ChatMessage,
    ChatInput,
    LoadingDots
  },
  data() {
    return {
      messages: [],
      memoryId: null,
      isAiTyping: false,
      isStreaming: false,
      currentAiResponse: '',
      currentEventSource: null,
      connectionError: false
    }
  },
  mounted() {
    // 初始化聊天
    this.initializeChat()
  },
  computed: {
    currentAiResponseRendered() {
      if (!this.currentAiResponse) return ''
      // 配置marked选项
      marked.setOptions({
        breaks: true, // 支持换行
        gfm: true, // 支持GitHub风格的Markdown
        sanitize: false, // 不过滤HTML（根据需要可以开启）
        highlight: function(code, lang) {
          // 可以在这里添加代码高亮功能
          return code
        }
      })
      return marked(this.currentAiResponse)
    }
  },
  methods: {
    initializeChat() {
      // 生成新的聊天室ID
      this.memoryId = generateMemoryId()
      this.messages = []
      this.connectionError = false
    },
    
    sendMessage(message) {
      if (!message.trim()) return
      
      // 添加用户消息
      this.messages.push({
        id: Date.now(),
        content: message,
        isUser: true,
        timestamp: new Date()
      })
      
      // 滚动到底部
      this.scrollToBottom()
      
      // 开始AI回复
      this.isAiTyping = true
      this.isStreaming = true
      this.currentAiResponse = ''
      this.connectionError = false
      
      // 调用SSE接口
      this.currentEventSource = chatWithSSE(
        this.memoryId,
        message,
        (chunk) => {
          // 处理AI回复
          this.currentAiResponse += chunk
          this.scrollToBottom()
        },
        (error) => {
          // 处理错误
          console.error('AI 回复出错:', error)
          this.connectionError = true
          this.isAiTyping = false
          this.isStreaming = false
        },
        () => {
          // 连接关闭
          this.isAiTyping = false
          this.isStreaming = false
          
          // 添加AI回复到消息列表
          if (this.currentAiResponse) {
            this.messages.push({
              id: Date.now() + 1,
              content: this.currentAiResponse,
              isUser: false,
              timestamp: new Date()
            })
            this.scrollToBottom()
            this.currentAiResponse = ''
          }
        }
      )
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    }
  },
  beforeUnmount() {
    // 组件卸载时关闭SSE连接
    if (this.currentEventSource) {
      this.currentEventSource.close()
    }
  }
}
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  color: #333;
  line-height: 1.6;
}

/* 认证界面容器 */
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

/* 主应用容器 */
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 头部 */
.app-header {
  background-color: #4CAF50;
  color: white;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.app-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.app-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 10px;
}

.welcome-text {
  font-size: 14px;
}

.logout-btn {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

/* 聊天容器 */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
  background-color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 消息容器 */
.messages-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 欢迎消息 */
.welcome-message {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.welcome-content {
  max-width: 500px;
  margin: 0 auto;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.welcome-message h2 {
  color: #333;
  margin-bottom: 15px;
}

.welcome-message p {
  margin-bottom: 15px;
}

.welcome-message ul {
  text-align: left;
  margin: 20px auto;
  max-width: 300px;
}

.welcome-message li {
  margin-bottom: 8px;
}

/* 聊天消息 */
.chat-message {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.chat-message.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.user-avatar {
  background-color: #4CAF50;
  color: white;
}

.ai-avatar {
  background-color: #2196F3;
  color: white;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.chat-message.user-message .message-content {
  text-align: right;
}

.message-bubble {
  display: inline-block;
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.4;
}

.chat-message.user-message .message-bubble {
  background-color: #e3f2fd;
  border-bottom-right-radius: 4px;
}

.chat-message.ai-message .message-bubble {
  background-color: #f1f1f1;
  border-bottom-left-radius: 4px;
}

.message-markdown {
  text-align: left;
  white-space: pre-wrap;
}

.message-markdown code {
  background-color: #f4f4f4;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9em;
}

.message-markdown pre {
  background-color: #f4f4f4;
  padding: 10px;
  border-radius: 5px;
  overflow-x: auto;
  margin: 10px 0;
}

.message-markdown pre code {
  background-color: transparent;
  padding: 0;
}

/* 输入区域 */
.chat-input-container {
  padding: 20px;
  border-top: 1px solid #eee;
  background-color: #f9f9f9;
}

.chat-input-form {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 12px 16px;
  resize: none;
  font-size: 16px;
  font-family: inherit;
  min-height: 40px;
  max-height: 120px;
  outline: none;
  transition: border-color 0.3s;
}

.chat-input:focus {
  border-color: #4CAF50;
}

.send-button {
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
  flex-shrink: 0;
}

.send-button:hover:not(:disabled) {
  background-color: #45a049;
}

.send-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 加载动画 */
.loading-dots {
  display: inline-flex;
  gap: 4px;
  margin-top: 8px;
}

.loading-dot {
  width: 8px;
  height: 8px;
  background-color: #666;
  border-radius: 50%;
  animation: loading 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loading {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 连接错误提示 */
.connection-error {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #ffebee;
  color: #d32f2f;
  padding: 12px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-icon {
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    max-width: 100%;
    height: 100vh;
  }
  
  .app-header {
    padding: 15px;
  }
  
  .app-title {
    font-size: 24px;
  }
  
  .messages-container {
    padding: 15px;
  }
  
  .chat-input-container {
    padding: 15px;
  }
  
  .message-content {
    max-width: 80%;
  }
}
</style>
