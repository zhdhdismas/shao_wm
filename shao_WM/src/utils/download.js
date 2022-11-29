import axios from 'axios'

const service = axios.create({
  responseType: 'arraybuffer'
})
 // 如果是开发环境,下面这行不用.baseURL='/'
//  axios.defaults.baseURL = 'http://121.196.209.48:8081';
service.interceptors.request.use(

    config => {
      if (!config.url.endsWith('/login')) {
          if(localStorage.getItem('token')!=null){
              const token='Bearer '+localStorage.getItem('token');
              config.headers.common['Authorization']=token;
          }
      }  
      return config;
  
    },
);

service.interceptors.response.use(
  resp => {
    const headers = resp.headers;
    let reg = RegExp(/application\/json/);
    if (headers['content-type'].match(reg)) {
      resp.data = uintToString(resp.data);
    } else {
      let fileDownload = require('js-file-download');
      let fileName = headers["content-disposition"].split(";")
        [1].split("filename=")[1];
      let contentType = headers["content-type"];
      fileName = decodeURIComponent(fileName);
      fileDownload(resp.data, fileName, contentType)
    }
  },error => {
    console.log(error);
  }
);
let base = '';
export const downloadRequest = (url, params) => {
  return service({
    method: 'get',
    url: `${base}${url}`,
    data: params
  })
}
function uintToString(uintArray) {
  let encodedString = String.fromCharCode.apply(null, new
    Uint8Array(uintArray)),
    decodedString = decodeURIComponent(escape(encodedString));
  return JSON.parse(decodedString);
}
export default service