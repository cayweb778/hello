function getXInfo(){
  const axxx = process.memoryUsage()
  const bbb = {
    "rss": axxx.rss / 1024 / 1024+"m",
    "heapTotal": axxx.heapTotal / 1024 / 1024+"m",
    "heapUsed": axxx.heapUsed / 1024 / 1024+"m",
    "external": axxx.external / 1024 / 1024+"m",
    "arrayBuffers": axxx.arrayBuffers / 1024 / 1024+"m"
  }
  console.log(bbb)
}
getXInfo()
