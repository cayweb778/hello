function defineStore(params) {
    return {}
}


function createPingzhengStore() {

    const pingzhengStore = defineStore({
        state: () => ({
            coId: '',
            coInfo: {},
            api: {
                zhaiYao() {

                },
                kuaiJIKeMu() {

                },
                dept() {

                },
                defaultDate() {

                },
                inoId() {

                }
            },
            data: {}
        }),
        action: {
            updateInfo(params) {
                const {apiList, row} = params
                const {
                    zhaiYaoList,
                    kuaiJiKeMuList,
                    jieSuanFangShiList
                } = apiList
            }
        }
    })

    return pingzhengStore
}

const pingzhengStore = createPingzhengStore()

// 差异替换
const pingzhengFillinStore = defineStore({
    state: () => ({
        coList: [
            {}
        ],
        currentCoInfo: {
            info: {},
        },
        pingzhengStore
    }),
    action: {
        changeCo(coInfo) {
            this.pingzhengStore.updateInfo(coInfo)
        }
    }
})
