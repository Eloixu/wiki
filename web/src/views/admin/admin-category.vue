<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
        <a-form layout="inline" :model="param">
            <a-form-item>
                <a-input v-model:value="param.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
                <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})" >
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
                :columns="columns"
                :row-key="record => record.id"
                :data-source="categorys"
                :pagination="pagination"
                :loading="loading"
                @change="handleTableChange"
        >
            <template #cover="{ text: cover }">
                <img v-if="cover" :src="cover" alt="avatar" />
            </template>
            <template v-slot:action="{ text, record }">
                <a-space size="small">
                    <a-button type="primary" @click="edit(record)">
                        编辑
                    </a-button>
                    <a-popconfirm
                            title="删除后不可恢复，确认删除?"
                            ok-text="Yes"
                            cancel-text="No"
                            @confirm="del(record.id)"
                    >
                        <a-button type="danger">
                            删除
                        </a-button>
                    </a-popconfirm>
                </a-space>
            </template>
        </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="分类表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
      <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="名称">
              <a-input v-model:value="category.name" />
          </a-form-item>
          <a-form-item label="父分类">
              <a-input v-model:value="category.parent" />
          </a-form-item>
          <a-form-item label="顺序">
              <a-input v-model:value="category.sort" />
          </a-form-item>
      </a-form>
  </a-modal>

</template>

<script lang="ts">
    import { defineComponent, onMounted, ref } from 'vue';
    import { message } from 'ant-design-vue';
    import axios from 'axios';
    import {Tool} from "@/util/tool";

    export default defineComponent({
        name: 'AdminCategory',
        setup() {
            const param = ref({});
            const categorys = ref();
            const pagination = ref({
                current: 1,
                pageSize: 10,
                total: 0
            });
            const loading = ref(false);

            const columns = [
                {
                    title: '名称',
                    dataIndex: 'name'
                },
                {
                    title: '父分类',
                    key: 'parent',
                    dataIndex: 'parent'
                },
                {
                    title: '顺序',
                    dataIndex: 'sort'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: { customRender: 'action' }
                }
            ];

            /**
             * 数据查询
             **/
            const handleQuery = (params: any) => {
                loading.value = true;
                axios.get("/category/list", {
                    params:{
                        page: params.page,
                        size: params.size,
                        name: param.value.name
                    }
                }).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if(data.success){
                        //把response里的data的值赋值给响应变量categorys
                        categorys.value = data.content.list;
                        // 重置分页按钮(若不写则会一直在第一页)
                        pagination.value.current = params.page;
                        //这个total会传到table标签的pagination从而自动计算页数
                        pagination.value.total = data.content.total;
                    }else{
                        message.error(data.message);
                    }

                });
            };

            /**
             * 表格点击页码时触发
             */
            const handleTableChange = (pagination: any) => {
                console.log("看看自带的分页参数都有啥：" + pagination);
                handleQuery({
                    page: pagination.current,
                    size: pagination.pageSize
                });
            };

            // -------- 表单 ---------
            const category = ref({});
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                //打开加载效果
                modalLoading.value = true;
                axios.post("/category/save",category.value).then((response) => {
                    //关闭加载效果
                    modalLoading.value = false;
                    const data = response.data;
                    if(data.success){
                        //关闭对话框
                        modalVisible.value = false;
                        //重新加载列表
                        handleQuery({
                            //下面的参数会作为params传递到handleQuery方法里去
                            page: pagination.value.current,
                            //pagination是响应式变量，取值一定要加.value
                            size: pagination.value.pageSize
                        });
                    }else{
                        message.error(data.message);
                    }
                });
            };


            /**
             * 编辑
             */
            const edit = (record: any) => {
                modalVisible.value = true;
                //通过赋值record的值给category，这样修改category时就不会对record产生影响了
                category.value = Tool.copy(record);
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;
                //把category设成空对象
                category.value={}
            };

            /**
             * 删除
             */
            const del = (id: number) => {
                axios.delete("/category/delete/"+id).then((response) => {
                    const data = response.data;
                    if(data.success){
                        //重新加载列表
                        handleQuery({
                            //下面的参数会作为params传递到handleQuery方法里去
                            page: pagination.value.current,
                            //pagination是响应式变量，取值一定要加.value
                            size: pagination.value.pageSize
                        });
                    }
                });
            };

            onMounted(() => {
                handleQuery({
                    //下面的参数会作为params传递到handleQuery方法里去
                    page: 1,
                    //pagination是响应式变量，取值一定要加.value
                    size: pagination.value.pageSize
                });
            });

            return {
                param,
                categorys,
                pagination,
                columns,
                loading,
                handleTableChange,
                handleQuery,

                edit,
                add,
                del,

                category,
                modalVisible,
                modalLoading,
                handleModalOk,
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