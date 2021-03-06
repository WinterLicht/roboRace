const _ = require('lodash')

const defaultHeader = {
  credentials: "same-origin"
}

function headers(additional){
  return _.merge({}, defaultHeader, additional)
}


function parseJson(resp) {
  if(resp.status - resp.status % 100 === 200)
    return resp.json()
  else {
    const data = resp.text()
    data.then(text => window.alert(`there was an unexpected problem with a service request. Response: (${resp.status}) ${text}`))
    return Promise.reject(data)
  }
}

module.exports = {headers, parseJson}
