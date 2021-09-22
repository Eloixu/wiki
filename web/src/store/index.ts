import { createStore } from 'vuex'

const store =  createStore({
  state: {//定义全局变量
    user: {}
  },
  mutations: {//对变量进行操作（同步）
    setUser (state, user) {
      state.user = user;
    }
  },
  actions: {//对变量进行操作（异步）
  },
  modules: {
  }
});

export default store;
