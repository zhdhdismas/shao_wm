let proxyObj = {}
proxyObj['/ws'] = {
    ws: false,
    target: 'ws://localhost:8081'
};
proxyObj['/'] = {
//websocket
  ws: false,
//目标地址
  target: 'http://localhost:8081',
//发送请求头中host会设置成target
  changeOrigin: true,
//不重写请求地址
  pathRewrite: {
    '^/': '/'
  },
  autoOpenBrowser: false,
  errorOverlay: true,
  notifyOnErrors: true,
  poll: false, 
}
module.exports = {
  devServer: {
    host: 'localhost',
    port: 8080,
    proxy: proxyObj,
   
  }
}
