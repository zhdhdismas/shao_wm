import axios from 'axios';
axios.interceptors.response.use((resp) => {
       if (resp.data.status=="over") {   //接口请求成功，业务逻辑错误
          window.location.href="/#/?over=1";
        return;
       }
    return resp;
 }, (error) => {
    
 });
 
 
let base = 'http://localhost:8081';
axios.defaults.withCredentials = true;
axios.interceptors.request.use(

  config => {
    if (!config.url.endsWith('/login')) {
        if(localStorage.getItem('token')!=null){
            const token='Bearer '+localStorage.getItem('token')
            config.headers.common['Authorization']=token
        }       
    }
    return config;

  },

  error => {
    return Promise.reject(error);
  }

);
export const postRequest2 = (url, params) => {
    return axios({
  
      method: 'post',
      url: `${base}${url}`,
      data: params,
     
      headers: {
        'Content-Type': 'application/json',
      }
    });
  
  }
export const postRequest = (url, params) => {
  return axios({

    method: 'post',
    url: `${base}${url}`,
    data: params,
    transformRequest: [function (data) {
      // Do whatever you want to transform the data
      let ret = ''
      for (let it in data) {
        ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
      }
      return ret
    }],
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    }
  });

}
export const uploadFileRequest = (url, params) => {
  return axios({
    method: 'post',
    url: `${base}${url}`,
    data: params,


    headers: {
      'Content-Type': 'multipart/form-data',
    }
  });
}
export const putRequest = (url, params) => {
  return axios({
    method: 'put',
    url: `${base}${url}`,
    data: params,
    transformRequest: [function (data) {
      let ret = ''
      for (let it in data) {
        ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
      }
      return ret
    }],
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    }
  });
}
export const deleteRequest = (url, params) => {
  return axios({
    method: 'delete',
    url: `${base}${url}`,
    data: params,
    transformRequest: [function (data) {
      let ret = ''
      for (let it in data) {
        ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
      }
      return ret
    }],
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    }
  });
}
export const getRequest = (url, params) => {
  return axios({
    method: 'get',
    data: params,
    transformRequest: [function (data) {
      let ret = ''
      for (let it in data) {
        ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
      }
      return ret
    }],
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    url: `${base}${url}`
  });
}
export function getUUID(len, radix) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('')
    var uuid = []
    var i
    radix = radix || chars.length
    if (len) {
      for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix]
    } else {
      var r
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-'
      uuid[14] = '4'
      for (i = 0; i < 36; i++) {
        if (!uuid[i]) {
          r = 0 | Math.random() * 16
          uuid[i] = chars[(i === 19) ? (r & 0x3) | 0x8 : r]
        }
      }
    }
    return uuid.join('')
  }
  
 
