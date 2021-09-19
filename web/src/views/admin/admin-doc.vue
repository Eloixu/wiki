<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
        <a-row :gutter="24">
            <a-col :span="8">
                <a-form layout="inline" :model="param">
                    <a-form-item>
                        <a-button type="primary" @click="handleQuery()" >
                            查询
                        </a-button>
                    </a-form-item>
                    <a-form-item>
                        <a-button type="primary" @click="add"  >
                            新增
                        </a-button>
                    </a-form-item>
                </a-form>
                <a-table
                        v-if="level1.length > 0"
                        :columns="columns"
                        :row-key="record => record.id"
                        :data-source="level1"
                        :pagination="false"
                        :loading="loading"
                        size="small"
                        :defaultExpandAllRows="true"
                >
                    <template #name="{ text, record }">
                        {{record.sort}} {{text}}
                    </template>
                    <template v-slot:action="{ text, record }">
                        <a-space size="small">
                            <a-button type="primary" @click="edit(record)" size="small">
                                编辑
                            </a-button>
                            <a-popconfirm
                                    title="删除后不可恢复，确认删除?"
                                    ok-text="Yes"
                                    cancel-text="No"
                                    @confirm="del(record.id)"
                            >
                                <a-button type="danger" size="small">
                                    删除
                                </a-button>
                            </a-popconfirm>
                        </a-space>
                    </template>
                </a-table>
            </a-col>
            <a-col :span="16">
                <p>
                    <a-form layout="inline" :model="param">
                        <a-form-item>
                            <a-button type="primary" @click="handleSave()">
                                保存
                            </a-button>
                        </a-form-item>
                    </a-form>
                </p>
                <a-form :model="doc" layout="vertical">
                    <a-form-item>
                        <a-input v-model:value="doc.name" placeholder="名称"/>
                    </a-form-item>
                    <a-form-item>
                        <a-tree-select
                                v-model:value="doc.parent"
                                style="width: 100%"
                                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                                :tree-data="treeSelectData"
                                placeholder="请选择父文档"
                                tree-default-expand-all
                                :replaceFields="{title:'name',key:'id',value:'id'}"
                        >
                        </a-tree-select>
                    </a-form-item>
                    <a-form-item>
                        <a-input v-model:value="doc.sort" placeholder="顺序"/>
                    </a-form-item>
                    <a-form-item>
                        <a-button type="primary" @click="handlePreviewContent()">
                            <EyeOutlined /> 内容预览
                        </a-button>
                    </a-form-item>
                    <a-form-item>
                        <div id="content"></div>
                    </a-form-item>
                </a-form>
            </a-col>
        </a-row>

        <!--滑动窗口，在visible为true时才会显示-->
        <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
            <!--wangeditor的样式是全局的，所以这里可以取到-->
            <div class="wangeditor" :innerHTML="previewHtml"></div>
        </a-drawer>

    </a-layout-content>
  </a-layout>

  <!--<a-modal-->
      <!--title="文档表单"-->
      <!--v-model:visible="modalVisible"-->
      <!--:confirm-loading="modalLoading"-->
      <!--@ok="handleModalOk"-->
  <!--&gt;-->

  <!--</a-modal>-->

</template>

<script lang="ts">
    import { defineComponent, onMounted, ref } from 'vue';
    import { message } from 'ant-design-vue';
    import axios from 'axios';
    import {Tool} from "@/util/tool";
    import {useRoute} from "vue-router";
    import E from 'wangeditor';


    export default defineComponent({
        name: 'AdminDoc',
        setup() {
            const route = useRoute();
            console.log("路由：", route);
            console.log("route.path：", route.path);
            console.log("route.query：", route.query);
            console.log("route.param：", route.params);
            console.log("route.fullPath：", route.fullPath);
            console.log("route.name：", route.name);
            console.log("route.meta：", route.meta);

            const param = ref({});
            const docs = ref();
            const loading = ref(false);

            const columns = [
                {
                    title: '名称',
                    dataIndex: 'name',
                    slots: { customRender: 'name' }
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: { customRender: 'action' }
                }
            ];
            /**
             * 一级文档树，children属性就是二级文档
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const level1 = ref(); // 一级文档树，children属性就是二级文档
            level1.value = []


            /**
             * 数据查询
             **/
            const handleQuery = () => {
                loading.value = true;
                axios.get("/doc/all/" + route.query.ebookId).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if(data.success){
                        //把response里的data的值赋值给响应变量docs
                        docs.value = data.content;
                        console.log("原始数组：", docs.value);

                        level1.value = [];
                        level1.value = Tool.array2Tree(docs.value, 0);
                        console.log("树形结构：", level1);

                        //父文档下拉框初始化
                        treeSelectData.value = Tool.copy(level1.value);
                        // 为选择树添加一个"无"
                        treeSelectData.value.unshift({id: 0, name: '无'});
                    }else{
                        message.error(data.message);
                    }

                });
            };


            // -------- 表单 ---------
            // 因为树选择组件的属性状态，会随当前编辑的节点而变化(打开不同的节点，disable的节点会变化)，所以单独声明一个响应式变量
            const treeSelectData = ref()
            treeSelectData.value = []
            const doc = ref();
            doc.value = {};
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const editor = new E('#content');
            editor.config.zIndex = 0;//富文本框放在最底下

            const handleSave = () => {
                //打开加载效果
                modalLoading.value = true;
                doc.value.content = editor.txt.html();
                axios.post("/doc/save",doc.value).then((response) => {
                    //关闭加载效果
                    modalLoading.value = false;
                    const data = response.data;
                    if(data.success){
//                        //关闭对话框
//                        modalVisible.value = false;
                        message.success("保存成功！");
                        //重新加载列表
                        handleQuery();
                    }else{
                        message.error(data.message);
                    }
                });
            };


            /**
             * 将某节点及其子孙节点全部置为disabled
             */
            const setDisable = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("disabled", node);
                        // 将目标节点设置为disabled
                        node.disabled = true;

                        // 遍历所有子节点，将所有子节点全部都加上disabled
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                setDisable(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            setDisable(children, id);
                        }
                    }
                }
            };

            /**
             * 查找整根树枝
             */
            const ids: Array<string> = [];
            const getDeleteIds = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        ids.push(id);

                        // 遍历所有子节点
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                getDeleteIds(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            getDeleteIds(children, id);
                        }
                    }
                }
            }

            /**
             * 内容查询
             **/
            const handleQueryContent = () => {
                axios.get("/doc/get-content", {
                    params:{
                        id: doc.value.id
                    }
                }).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        const data = response.data;
                        if (data.success) {
                            editor.txt.html(data.content)
                        } else {
                            message.error(data.message);
                        }
                    }
                });
            };


            /**
             * 编辑
             */
            const edit = (record: any) => {
                // 清空富文本框
                editor.txt.html("");
                modalVisible.value = true;
                //通过赋值record的值给doc，这样修改doc时就不会对record产生影响了
                doc.value = Tool.copy(record);
                handleQueryContent();

                // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
                treeSelectData.value = Tool.copy(level1.value);
                setDisable(treeSelectData.value, record.id);

                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});
            };

            /**
             * 新增
             */
            const add = () => {
                // 清空富文本框
                editor.txt.html("");
                modalVisible.value = true;
                //把doc设成空对象
                doc.value={
                    ebookId: route.query.ebookId
                };

                // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
                treeSelectData.value = Tool.copy(level1.value);
                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});
            };

            /**
             * 删除
             */
            const del = (id: number) => {
                // console.log(level1, level1.value, id)
                getDeleteIds(level1.value, id);
                console.log(ids)
                axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
                    const data = response.data;
                    if(data.success){
                        //重新加载列表
                        handleQuery();
                    }
                });
            };


            // ----------------富文本预览--------------
            const drawerVisible = ref(false);
            const previewHtml = ref();
            const handlePreviewContent = () => {
                const html = editor.txt.html();
                previewHtml.value = html;
                drawerVisible.value = true;
            };
            const onDrawerClose = () => {
                drawerVisible.value = false;
            };

            onMounted(() => {
                handleQuery();

                editor.create();
            });

            return {
                param,
                //docs,
                level1,
                columns,
                loading,
                handleQuery,

                edit,
                add,
                del,

                doc,
                modalVisible,
                modalLoading,
                handleSave,

                treeSelectData,

//                滑动窗口
                drawerVisible,
                previewHtml,
                handlePreviewContent,
                onDrawerClose,
            }
        }
    });
</script>

<style scoped>
    img {
        width: 50px;
        height: 50px;
    }
</style>