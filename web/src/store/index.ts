import { createStore } from 'vuex'

declare let SessionStorage: any;
const USER = "USER";

const store =  createStore({
  state: {//定义全局变量
    //若SessionStorage里获取不到USER对象，则赋值成空对象
    user: SessionStorage.get(USER) || {}
  },
  mutations: {//对变量进行操作（同步）
    setUser (state, user) {
      state.user = user;
      SessionStorage.set(USER,user);
    }
  },
  actions: {//对变量进行操作（异步）
  },
  modules: {
  }
});

export default store;
