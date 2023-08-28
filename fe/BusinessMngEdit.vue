<template>
  <section class="content">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <div class="card">
            <div class="col-12 mb-1 text-right top-button">
              <button class="btn button-main mr-2" @click="returnDetail">キャンセル</button>
              <button class="btn button-main" @click="onSubmit">保存</button>
            </div>
            <div class="card-header text-left">
              <div class="screen-name">業務台帳</div>
            </div>
            <!--業務台帳-->
            <!-- /.card-header -->
            <div v-if="showErrorPopup" id="popup_user_edit">
              <div id="popup_user_edit">
                <div id="popup-inner_user_edit">
                  <div>{{ errorMessage }}</div>
                  <button id="popup-close_user_edit" @click="showErrorPopup = false; cancel()">キャンセル</button>
                </div>
              </div>
            </div>
            <div class="card-body py-md-1">
              <v-form ref="form" v-model="form" v-slot="{ errors }">
                <div class="row content-data">
                  <div class="col-12 mb-3">
                    <error-message class="text-danger" name="category"></error-message>
                    <error-message class="text-danger" name="name"></error-message>
                    <error-message class="text-danger" name="description"></error-message>
                    <error-message class="text-danger" name="personNumber"></error-message>
                    <error-message class="text-danger" name="personName"></error-message>
                    <error-message class="text-danger" name="workingTime"></error-message>
                    <error-message class="text-danger" name="workingDays"></error-message>
                    <error-message class="text-danger" name="workingMonths"></error-message>
                    <error-message class="text-danger" name="provideToPersonNumber"></error-message>
                    <error-message class="text-danger" name="provideToPersonName"></error-message>
                    <error-message class="text-danger" name="department"></error-message>
                    <error-message class="text-danger" name="amountCategory"></error-message>
                    <error-message class="text-danger" name="dataCreation"></error-message>
                    <span v-if="this.rpaErrorMsg"  role="alert" class="text-danger">ロボット番号が存在していません。</span>
                  </div>
                  <div class="col-12 mb-3">
                    <table class="rpa-table-form">
                      <!--jobs.number-->
                      <tr>
                        <td>業務番号</td>
                        <!--                        <td><input :value="formData.number" name="number" disabled="" type="text" class="form-control"></td>-->
                        <td>
                          <input :value="formData.number" name="number" type="text" class="form-control" maxlength="8"
                                 disabled/>
                        </td>
                      </tr>
                      <tr>
                        <td>業務名<span class="text-danger">*</span></td>
                        <td>
                          <Field v-model="formData.name" :disabled="disableName" maxlength="200" name="name"
                                 rules="required:業務名" type="text" class="form-control"
                                 @keydown.enter.stop.prevent
                                 :class="{ 'is-invalid': errors.name }"
                          />
                        </td>
                      </tr>
                      <tr>
                        <td>業務ステータス</td>
                        <td>
                          <select v-model="formData.status" class="form-control" name="status" tabindex="1">
                            <option v-for="(ed2,i) in memo.status" v-bind:key="i" :value="ed2.id">{{
                                ed2.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                    </table>
                  </div>
                  <div class="col-12 mb-3 form-scroll">
                    <table class="rpa-table-form width_td_form_table">
                      <tr>
                        <td>マニュアル</td>
                        <td>
                          <div class="input-group">
                            <FileUpload
                                :disabled="uploadFileDisable"
                                @filesAdded="handleFile"
                                box-class="custom-file" input-class="custom-file-input"/>
                            <label class="custom-file-label">ファイルを選択</label>
                            <span v-if="err" class="text-danger p-2">ファイルをアップロードできません</span>
                            <div v-if="this.formData.jobRelated.attachment1"
                                 class="preview-name d-flex align-items-center justify-content-end pr-2">
                              <div class="mr-5">
                                {{ fileName }}
                              </div>
                              <div>
                                <i class="fas fa-trash-alt" @click="deleteFile"></i>
                              </div>
                            </div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>マニュアルファイルパス</td>
                        <td>
                          <Field v-model="formData.jobRelated.manualFilePath"
                                 :disabled="disableName"
                                 maxlength="1024"
                                 name="manualFilePath"
                                 type="text"
                                 class="form-control"
                          />
                        </td>
                      </tr>
                      <tr>
                        <td>カルテ</td>
                        <td>
                          <div class="input-group">
                            <FileUpload
                                :disabled="uploadFile2Disable"
                                @filesAdded="handleFile2"
                                box-class="custom-file" input-class="custom-file-input"/>
                            <label class="custom-file-label">ファイルを選択</label>
                            <span v-if="err" class="text-danger p-2">ファイルをアップロードできません</span>
                            <div v-if="this.formData.jobRelated.attachment2"
                                 class="preview-name d-flex align-items-center justify-content-end pr-2">
                              <div class="mr-5">
                                {{ fileName2 }}

                              </div>
                              <div>
                                <i class="fas fa-trash-alt" @click="deleteFile2"></i>
                              </div>
                            </div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td>部署番号</td>
                        <td><input :value="departmentNo" name="departmentNumber" disabled="" type="text"
                                   class="form-control"></td>
                      </tr>
                      <!--jobs.department-->
                      <tr>
                        <td>部署名<span class="text-danger">*</span></td>
                        <td>
                          <Field
                            v-model.number="formData.department"
                            as="select"
                            class="form-control"
                            name="department"
                            rules="required:部署名"
                            id="department"
                          >
                            <option></option>
                              <option
                                v-for="ed1 in memo.departments"
                                v-bind:key="ed1.id"
                                :value="ed1.id"
                              >
                                {{ ed1.name }}
                              </option>
                          </Field>
                        </td>
                      </tr>
                      <!--jobs.ledger_id-->
                      <tr>
                        <td>ロボット番号</td>
                        <td>
                          <input
                              v-model="formData.rpaNumber"
                              list="rpaNumbers"
                              type="text"
                              class="form-control"
                              name="rpaNumber"
                              maxlength="20"
                              autocomplete="off"
                              id="hide_r"/>
                          <datalist id="rpaNumbers">
                            <option></option>
                            <option v-for="(dp,i) in memo.ledger" v-bind:key="i" :value="dp.rpaNumber">
                              {{ dp.rpaNumber }}
                            </option>
                          </datalist>
                          <span v-if="errMessage" class="text-danger" role="alert">{{ errMessage }}</span>
                        </td>
                      </tr>
                      <!--jobs.category-->
                      <tr>
                        <td>カテゴリー</td>
                        <td>
                          <select v-model="formData.category" class="form-control"
                                  name="category">
                            <option></option>
                            <option v-for="dp in memo.categories" v-bind:key="dp.id" :value="dp.name">
                              {{ dp.name }}
                            </option>
                          </select>
                          <!-- <input v-model="formData.category" list="categories" maxlength="20" name="category"
                                 rules="maxlength:20,カテゴリー"
                                 autocomplete="off"
                                 @keydown.enter.stop.prevent
                                 type="text" class="form-control"
                                 id="hide_c">
                          <datalist id="categories">
                            <option></option>
                            <option v-for="dp in memo.categories" v-bind:key="dp.id" :value="dp.name">
                              {{ dp.name }}
                            </option>
                          </datalist> -->
                        </td>
                      </tr>
                      <tr>
                        <td>業務概要・動作</td>
                        <td>
                          <textarea v-model="formData.description" class="form-control" maxlength="1024"
                                    name="description"
                                    rules="maxlength:1024,業務概要・動作"></textarea>
                        </td>
                      </tr>
                      <tr>
                        <td>担当者社員番号</td>
                        <td>
                          <Field v-model="jriPersonNumber" disabled="" maxlength="10" name="personNumber"
                                 rules="alphanumeric:担当者社員番号" type="text" class="form-control"/>
                        </td>
                      </tr>
                      <tr>
                        <td>担当者氏名</td>
                        <td>
                          <Multiselect
                              name="personName"
                              v-model="formData.jobRpaInfo.person"
                              :options="this.memo.search_persons_status"
                              label="text"
                              mode="single"
                              searchable="true"
                              noResultsText="結果が見つかりません"
                              placeholder="--検索--"
                          />
                        </td>
                      </tr>
                      <tr>
                        <td>委託フラグ</td>
                        <td>
                          <select v-model="formData.jobRpaInfo.consignFlag" class="form-control" name="consignFlag"
                                  type="number"
                                  tabindex="1">
                            <option></option>
                            <option v-for="(ed2,i) in memo.consigns" v-bind:key="i" :value="ed2.id">{{ ed2.name }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>委託先</td>
                        <td>
                          <select v-model="formData.jobRpaInfo.consignee" class="form-control"
                                  name="consignee">
                            <option></option>
                            <option v-for="dp in memo.consignees" v-bind:key="dp" :value="dp.name">
                              {{ dp.name }}
                            </option>
                          </select>
                          <!-- <input v-model="formData.jobRpaInfo.consignee" list="consignee" maxlength="20"
                                 class="form-control"
                                 name="consignee"
                                 rules="maxlength:20,カテゴリー"
                                 autocomplete="off"
                                 @keydown.enter.stop.prevent
                                 type="text"
                                 id="hide_co"/>
                          <datalist id="consignee">
                            <option></option>
                            <option v-for="dp in memo.consignees" v-bind:key="dp" :value="dp.name">
                              {{ dp.name }}
                            </option>
                          </datalist> -->
                        </td>
                      </tr>
                      <tr>
                        <td>[A]1日あたりの作業時間（h）</td>
                        <td>
                          <Field v-model="formData.jobAnalyzeTime.workingTime" min="0" name="workingTime"
                                 class="form-control"
                                 rules="decimal:［A］1日あたりの作業時間(h)"
                                 @keydown.enter.stop.prevent
                                 step=".01" type="number"></Field>
                        </td>
                      </tr>
                      <tr>
                        <td>[B]1カ月あたりの作業日数（●日）</td>
                        <td>
                          <Field v-model="formData.jobAnalyzeTime.workingDays" min="0" name="workingDays"
                                 class="form-control"
                                 @keydown.enter.stop.prevent
                                 rules="decimal:［B］1カ月あたりの作業日数（●日）"
                                 step=".01"
                                 type="number"></Field>
                        </td>
                      </tr>
                      <tr>
                        <td>[C]1年あたりの作業月数（●カ月）</td>
                        <td>
                          <Field v-model="formData.jobAnalyzeTime.workingMonths" class="form-control"
                                 min="0" name="workingMonths"
                                 @keydown.enter.stop.prevent
                                 rules="numeric:［C］1年あたりの作業月数（●ヵ月）" type="number"/>
                        </td>
                      </tr>
                      <tr>
                        <td>年間稼働時間（ｈ）[A]ｘ[B]ｘ[C]</td>
                        <td>
                          <input :value="jobYearlyTime" name="yearlyTime" disabled="" type="number"
                                 class="form-control">
                        </td>
                      </tr>
                      <tr>
                        <td>改善前年間工数</td>
                        <td>
                          <input v-model="formData.jobAnalyzeTime.beforeKaizenTime" name="beforeKaizenTime" type="number"
                                 tabindex="23"
                                 class="form-control">
                        </td>
                      </tr>
                      <tr>
                        <td>削減工数</td>
                        <td>
                          <input :value="jobSakugenKosu" name="sakugenKosu" disabled type="number"
                                 class="form-control">
                        </td>
                      </tr>
                      <tr>
                        <td>最終改善日</td>
                        <td>
                          <input v-model="formData.jobAnalyzeTime.lastKaizenDate" name="lastKaizenDate" disabled type="text"
                                 class="form-control" />
                        </td>
                      </tr>
                      <tr>
                        <td>頻度単位</td>
                        <td>
                          <select v-model="formData.jobAnalyzeTime.jobInterval" class="form-control" name="interval"
                                  tabindex="24">
                            <option></option>
                            <option v-for="(ed2,i) in memo.jobintervals" v-bind:key="i" :value="ed2.id">{{
                                ed2.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>難易度</td>
                        <td>
                          <select v-model="formData.jobRelated.difficulty" class="form-control" name="difficulty"
                                  tabindex="26">
                            <option></option>
                            <option v-for="(ed2,i) in memo.difficulties" v-bind:key="i" :value="ed2.id">{{
                                ed2.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>マニュアル</td>
                        <td>
                          <select v-model="formData.jobRelated.manual" class="form-control" name="manual" tabindex="27">
                            <option></option>
                            <option v-for="(ed2,i) in memo.manuals" v-bind:key="i" :value="ed2.id">{{
                                ed2.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>定形/非定形</td>
                        <td>
                          <select v-model="formData.jobRelated.format" class="form-control" name="format" tabindex="27">
                            <option></option>
                            <option v-for="(ed2,i) in memo.formats" v-bind:key="i" :value="i">{{ ed2 }}</option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>属人度合</td>
                        <td>
                          <select v-model="formData.jobRelated.personalized" class="form-control" name="personalized"
                                  tabindex="27">
                            <option></option>
                            <option v-for="(ed2,i) in memo.personalizeds" v-bind:key="i" :value="ed2.id">{{
                                ed2.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td class="job-risk">リスクレベル</td>
                          <td>
                            <input :value="risklevelNo" name="risklevel" disabled="" type="text" tabindex="28"
                                   class="form-control">
                          </td>
                      </tr>
                      <tr>
                        <td class="job-risk">精算分類</td>
                        <td>
                          <select v-model.number="formData.jobRisk.paymentMethodId" class="form-control" name="paymentMethodId"
                                  tabindex="29">
                            <option></option>
                            <option v-for="item in memo.payment_methods" v-bind:key="item" :value="item.id">{{
                                item.paymentMethodName
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td class="job-risk">精算根拠書類1</td>
                        <td>
                          <div class="input-group">
                            <FileUpload
                                  :disabled="disabledPaymentMethod || uploadFile3Disable"
                                  @filesAdded="handleFile3"
                                  :box-class="['custom-file', { 'colorDisabled': disabledPaymentMethod }]" input-class="custom-file-input" tabindex="30"/>
                              <label :class="['custom-file-label', { 'colorDisabled': disabledPaymentMethod }]">ファイルを選択</label>
                              <span v-if="err" class="text-danger p-2">ファイルをアップロードできません</span>
                              <div v-if="this.formData.jobRisk.attachment1"
                                   class="preview-name d-flex align-items-center justify-content-end pr-2">
                                <div class="mr-5">
                                  {{ fileName3 }}
                                </div>
                                <div>
                                  <i class="fas fa-trash-alt" @click="deleteFile3"></i>
                                </div>
                              </div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                          <td class="job-risk">精算根拠書類ファイルパス1</td>
                            <td class="text-left">
                              <Field v-model="formData.jobRisk.manualFilePath1"                                 
                                        maxlength="1024"
                                        name="manualFilePath1"
                                        type="text"
                                        class="form-control"
                                        tabindex="31"
                                        :disabled="disabledPaymentMethod"
                              />
                          </td>
                      </tr>
                      <tr>
                        <td class="job-risk">精算根拠書類2</td>
                        <td>
                          <div class="input-group">
                            <FileUpload
                                  :disabled="disabledPaymentMethod || uploadFile4Disable"
                                  @filesAdded="handleFile4"
                                  :box-class="['custom-file', { 'colorDisabled': disabledPaymentMethod }]" input-class="custom-file-input" tabindex="32"/>
                              <label :class="['custom-file-label', { 'colorDisabled': disabledPaymentMethod }]">ファイルを選択</label>
                              <span v-if="err" class="text-danger p-2">ファイルをアップロードできません</span>
                              <div v-if="this.formData.jobRisk.attachment2"
                                   class="preview-name d-flex align-items-center justify-content-end pr-2">
                                <div class="mr-5">
                                  {{ fileName4 }}
                                </div>
                                <div>
                                  <i class="fas fa-trash-alt" @click="deleteFile4"></i>
                                </div>
                              </div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                          <td class="job-risk">精算根拠書類ファイルパス2</td>
                            <td class="text-left">
                              <Field v-model="formData.jobRisk.manualFilePath2"
                                     maxlength="1024"
                                     name="manualFilePath2"
                                     type="text"
                                     class="form-control"
                                     tabindex="33"
                                     :disabled="disabledPaymentMethod"
                              />
                            </td>
                      </tr>
                      <tr>
                          <td class="job-risk">金額<span v-if="!disabledPaymentMethod" class="text-danger">*</span></td>
                          <td>
                            <Field
                            v-model.number="formData.jobRisk.amountCategoryId"
                            as="select"
                            class="form-control"
                            name="amountCategory"
                            :rules="getValidationRule('金額')"
                            tabindex="34"
                            :disabled="disabledPaymentMethod"
                            >
                            <option></option>
                            <option v-for="item in memo.amount_categories" :key="item.id" :value="item.id">{{ item.amountCategoryName }}</option>
                            </Field>
                          </td>
                      </tr>
                      <tr>
                          <td class="job-risk">データ作成方法<span v-if="!disabledPaymentMethod" class="text-danger">*</span></td>
                          <td>
                              <Field
                              v-model.number="formData.jobRisk.dataCreationId"
                              as="select"
                              class="form-control"
                              name="dataCreation"
                              :rules="getValidationRule('データ作成方法')"
                              tabindex="35"
                              :disabled="disabledPaymentMethod"
                              >
                              <option></option>
                              <option v-for="item in memo.data_creations" :key="item.id" :value="item.id">{{ item.dataCreationName }}</option>
                              </Field>
                          </td>
                      </tr>
                      <tr>
                        <td>提供元（データ提供、前作業）</td>
                        <td>
                          <select v-model="formData.jobRelated.provideFrom" class="form-control" name="provideFrom"
                                  tabindex="36">
                            <option></option>
                            <option v-for="item in memo.provideFrom" v-bind:key="item.id" :value="item.id">{{
                                item.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>提供先（電話・メール対応）</td>
                        <td>
                          <select v-model="formData.jobRelated.provideTo" class="form-control" name="provideTo"
                                  tabindex="37">
                            <option></option>
                            <option v-for="item in memo.provideTo" v-bind:key="item.id" :value="item.id">{{
                                item.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>提供方法</td>
                        <td>
                          <select v-model="formData.jobRelated.provideMethod" class="form-control" name="provideMethod"
                                  tabindex="38">
                            <option></option>
                            <option v-for="item in memo.provideMethod" v-bind:key="item.id" :value="item.id">{{
                                item.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>提供先詳細</td>
                        <td>
                          <select v-model="formData.jobRelated.provideToDetail" class="form-control"
                                  name="provideToDetail"
                                  tabindex="39">
                            <option></option>
                            <option v-for="item in memo.provideToDetail" v-bind:key="item.id" :value="item.id">{{
                                item.name
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>責任者社員番号（社内主担当者）</td>
                        <td>
                          <Field v-model="jrPersonNumber" class="form-control" maxlength="10"
                                 name="mainPersonNumber"
                                 type="text" disabled/>
                        </td>
                      </tr>
                      <tr>
                        <td>責任者社員名（社内主担当者）</td>
                        <td>
                          <Multiselect
                              name="mainPerson"
                              v-model="formData.jobRelated.mainPerson"
                              :options="this.memo.search_persons_status"
                              label="text"
                              mode="single"
                              searchable="true"
                              noResultsText="結果が見つかりません"
                              placeholder="--検索--"
                          />
                        </td>
                      </tr>
                      <tr>
                        <td>使用Access拡張子</td>
                        <td>
                          <select v-model="formData.jobRelated.accessExtension" class="form-control"
                                  name="accessExtension"
                                  tabindex="40">
                            <option></option>
                            <option v-for="(ed2,i) in memo.accessExtension" v-bind:key="i" :value="i">{{
                                ed2
                              }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      <tr>
                        <td>サブ担当１</td>
                        <td>
                          <Multiselect
                              name="subPerson1"
                              v-model="formData.jobRelated.subPerson1"
                              :options="this.memo.search_persons_status"
                              label="text"
                              mode="single"
                              searchable="true"
                              noResultsText="結果が見つかりません"
                              placeholder="--検索--"
                              tabindex="41"
                          />
                        </td>
                      </tr>
                      <tr>
                        <td>サブ担当２</td>
                        <td>
                          <Multiselect
                              name="subPerson2"
                              v-model="formData.jobRelated.subPerson2"
                              :options="this.memo.search_persons_status"
                              label="text"
                              mode="single"
                              searchable="true"
                              noResultsText="結果が見つかりません"
                              placeholder="--検索--"
                              tabindex="42"
                          />
                        </td>
                      </tr>
                      <tr>
                        <td>サブ担当３</td>
                        <td>
                          <Multiselect
                              name="subPerson3"
                              v-model="formData.jobRelated.subPerson3"
                              :options="this.memo.search_persons_status"
                              label="text"
                              mode="single"
                              searchable="true"
                              noResultsText="結果が見つかりません"
                              placeholder="--検索--"
                              tabindex="43"
                          />
                        </td>
                      </tr>
                      <tr>
                        <td>備考１</td>
                        <td>
                          <textarea v-model="formData.jobRelated.remark1"
                                    class="form-control" maxlength="1024"
                                    name="remark1"
                                    rules="maxlength:1024,業務概要・動作"/>
                        </td>
                      </tr>
                      <tr>
                        <td>備考２</td>
                        <td>
                          <textarea v-model="formData.jobRelated.remark2"
                                    class="form-control" maxlength="1024"
                                    name="remark2"
                                    rules="maxlength:1024,業務概要・動作"/>
                        </td>
                      </tr>
                    </table>
                  </div>
                </div>
              </v-form>
            </div>
            <!-- /.card-body -->
          </div>
        </div>
        <!-- /.col -->
      </div>
      <!-- /.row -->
    </div>
    <!-- /.container-fluid -->
  </section>
</template>

<script>
import FileUpload from "@/components/molecules/FileUpload";
import {RPAApi} from "@/constants/api-url";
import RequestUtil from "@/utils/request-util";
import FnCommon from "@/utils/fn-common";
import Modal from "@/components/molecules/Modal";
import {RPAMessage} from "@/constants/message";
import Multiselect from '@vueform/multiselect';
import $ from 'jquery'
import { toast } from '@/plugins/toast';
import axios from "axios";
function setTwoNumberDecimal(event) {
  this.value = parseFloat(this.value).toFixed(2);
}

export default {
  name: "BusinessMngEdit",
  components: {
    FileUpload,
    Multiselect
  },
  data() {
    return {
      coutCheckNumber: 0,
      rpaErrorMsg: false,
      errMessage: null,
      disableName: false,
      uploadFile: null,
      uploadFileDisable: false,
      uploadFile2Disable: false,
      uploadFile3Disable: false,
      uploadFile4Disable: false,
      err: false,
      dbBeforeKaizenTime: null,
      dbWorkingTime: null,
      dbWorkingDays: null,
      dbWorkingMonths: null,
      dbSakugenKosu: null,
      disableDepartment:  true,
      memo: {
        status: [],
        departments: [],
        categories: [],
        persons: [],
        consigns: [],
        consignees: [],
        jobintervals: [],
        difficulties: [],
        manuals: [],
        formats: [],
        personalizeds: [],
        provideFrom: [],
        provideTo: [],
        provideMethod: [],
        provideToDetail: [],
        accessExtension: [],
        ledger: [],
        search_persons: [],
        search_persons_status: [],
        amount_categories: [],
        data_creations: [],
        payment_methods: [],
      },
      formData: {
        status: 1,
        jobRisk:{
          attachment1: null, attachment2: null, manualFilePath1: null, manualFilePath2: null,
          riskLevel: null, amountCategoryId: null, paymentMethodId: null, dataCreationId: null,
          pathAttachment1Del: null,pathAttachment2Del: null,
        },
        jobRelated: {
          attachment1: null, attachment2: null, difficulty: null, manual: null, format: null, personalized: null,
          provideFrom: null, provideTo: null, provideMethod: null, provideToDetail: null,
          mainPerson: null, accessExtension: null,
          subPerson1: null, subPerson2: null, subPerson3: null,
          remark1: null, remark2: null,
          pathAttachment1Del: null,
          pathAttachment2Del: null,
          manualFilePath: null
        },
        department: null,
        departmentNumber: null,
        number: null,
        ledgerId: null,
        rpaNumber: null,
        category: null,
        name: null,
        description: null,
        personNumber: null,
        version: null,
        jobRpaInfo: {
          person: null, consignFlag: null, consignee: null
        },
        ledgersLink: {
          createdAt: null, id: null, is_deleted: null, ledgerId: null, updatedAt: null
        },
        jobAnalyzeTime: {
          workingTime: null, workingDays: null, workingMonths: null, yearlyTime: null, jobInterval: null, beforeKaizenTime: null, sakugenKosu: null, lastKaizenDate: null
        }
      },
      showErrorPopup: false
    }
  },
  mounted() {
    $(document).on('click','body *',function(){
      if (!$(event.target).closest('#hide_r').length) {
        $('#rpaNumbers').css('display', 'none');
      }
      if (!$(event.target).closest('#hide_c').length) {
        $('#categories').css('display', 'none');
      }
      if (!$(event.target).closest('#hide_co').length) {
        $('#consignee').css('display', 'none');
      }
    });
    $(document).on('click','#hide_r',function(){
      $('#rpaNumbers').css('display', 'block');
    });
    $(document).on('click','#hide_c',function(){
      $('#categories').css('display', 'block');
    });
    $(document).on('click','#hide_co',function(){
      $('#consignee').css('display', 'block');
    });
    $( "#rpaNumbers" ).click(function(e) {
      this.formData.rpaNumber = e.target.value;
    }.bind(this));
    $( "#categories" ).click(function(e) {
      this.formData.category = e.target.value;
    }.bind(this));
    $( "#consignee" ).click(function(e) {
      this.formData.jobRpaInfo.consignee = e.target.value;
    }.bind(this));
    Object.keys(this.memo).forEach(v => {
      RequestUtil.get(RPAApi.MEMO(v)).then(res => {
        if (res.status === 200) {
          this.memo[v] = res.data.body
        }
      })
    });
    RequestUtil.get(RPAApi.GET_ROLE).then((res) => {
            if (res.status === 200) {
              res = res.data;
              if(res == 3 || res == 2){
                if(this.formData.department != null) {
                  this.disableDepartment = true;
                }
              } else {
                this.disableDepartment = false;
              }
            } else if(res.status === 404){
              return this.$router.push('login');
            }
          });
  },
  beforeMount() {
    RequestUtil.get(RPAApi.DETAIL_JOBS(this.$route.params.id)).then(res => {
      var res = res.data.body;
      FnCommon.copyProperties(this.formData, res);
      Object.assign(this.formData, res);

      this.formData.status = res?.mjobStatuse.id;
      this.formData.department = res?.mdepartment?.id;
      this.formData.departmentNumber = res?.mdepartment?.number;
      this.formData.personNumber = res?.jobRpaInfo?.mperson?.personNumber;
      this.formData.version = res?.version;
      this.formData.jobRpaInfo.person = res?.jobRpaInfo?.mperson?.id;
      this.formData.jobRpaInfo.consignFlag = res?.jobRpaInfo?.mconsign?.id;
      this.formData.jobRpaInfo.consignee = res?.jobRpaInfo?.consignee;
      this.formData.jobAnalyzeTime.jobInterval = res?.jobAnalyzeTime?.mjobInterval?.id;
      // this.formData.jobAnalyzeTime.yearlyTime = res?.jobAnalyzeTime?.yearlyTime / 60;
      // if (res?.jobAnalyzeTime?.sakugenKosu) {
      //   this.formData.jobAnalyzeTime.sakugenKosu = Number(res.jobAnalyzeTime.sakugenKosu / 60).toFixed(2);
      // }
      if (res?.jobAnalyzeTime?.beforeKaizenTime) {
        this.formData.jobAnalyzeTime.beforeKaizenTime = Number(res.jobAnalyzeTime.beforeKaizenTime / 60).toFixed(2);
      }
      if (res?.jobAnalyzeTime?.workingTime) {
        this.formData.jobAnalyzeTime.workingTime = Number(res.jobAnalyzeTime.workingTime / 60).toFixed(2);
      }
      if (res?.jobAnalyzeTime?.workingDays) {
        this.formData.jobAnalyzeTime.workingDays = Number(res.jobAnalyzeTime.workingDays / 24).toFixed(2);
      }
      // set data from DB
      this.dbWorkingDays = this.formData.jobAnalyzeTime.workingDays;
      this.dbWorkingMonths = this.formData.jobAnalyzeTime.workingMonths;
      this.dbWorkingTime = this.formData.jobAnalyzeTime.workingTime;
      this.dbBeforeKaizenTime = this.formData.jobAnalyzeTime.beforeKaizenTime;
      this.dbSakugenKosu = this.formData.jobAnalyzeTime.sakugenKosu;

      this.formData.jobRelated.personalized = res?.jobRelated?.mpersonalized?.id;
      this.formData.jobRelated.provideFrom = res?.jobRelated?.mprovideFrom?.id;
      this.formData.jobRelated.provideTo = res?.jobRelated?.mprovideTo?.id;
      this.formData.jobRelated.provideMethod = res?.jobRelated?.mprovideMethod?.id;
      this.formData.jobRelated.provideToDetail = res?.jobRelated?.mprovideToDetail?.id;
      this.formData.jobRelated.accessExtension = res?.jobRelated?.accessExtension;
      this.formData.jobRelated.mainPerson = res?.jobRelated?.mperson?.id;
      this.formData.jobRelated.subPerson1 = res?.jobRelated?.subPerson1?.id;
      this.formData.jobRelated.subPerson2 = res?.jobRelated?.subPerson2?.id;
      this.formData.jobRelated.subPerson3 = res?.jobRelated?.subPerson3?.id;
      if (!res?.jobRisk) {
        res.jobRisk = {
          riskLevel: null,
          attachment1: null,
          attachment2: null,
          mriskAmountCategory: { amountCategoryName: null },
          mriskDataCreation: { dataCreationName: null },
          mriskPaymentMethod: { paymentMethodName: null }
        };
      }
      if (!this.formData.jobRisk) {
        this.formData.jobRisk = {};
      }
      this.formData.jobRisk.amountCategoryId = res?.jobRisk?.mriskAmountCategory?.id;
      this.formData.jobRisk.dataCreationId = res?.jobRisk?.mriskDataCreation?.id;
      this.formData.jobRisk.paymentMethodId = res.jobRisk.mriskPaymentMethod.id;
      this.formData.jobRisk.riskLevel = res?.jobRisk?.mriskAmountCategory?.riskLevel;
      if (res?.ledgersLink?.id) {
        RequestUtil.get(RPAApi.DETAIL_LEDGERS_BY_LEDGER_LINK(res?.ledgersLink.id)).then(resLedger => {
          if (resLedger.status === 200) {
            this.formData.rpaNumber = resLedger.data.body.rpaNumber;
          }
        });
      }
    })
  },
  computed: {
    departmentNo() {
      return this.memo.departments?.find(e => e.id == this.formData.department)?.number;
    },
    risklevelNo() {
      this.formData.jobRisk.riskLevel = this.memo.amount_categories?.find(e => e.id == this.formData.jobRisk.amountCategoryId)?.riskLevel;
      return this.memo.amount_categories?.find(e => e.id == this.formData.jobRisk.amountCategoryId)?.riskLevel;
    },
    jobYearlyTime() {
      return Number(this.formData.jobAnalyzeTime.workingTime * this.formData.jobAnalyzeTime.workingDays * this.formData.jobAnalyzeTime.workingMonths).toFixed(2);

    },
    jobSakugenKosu() {
      return Number(this.formData.jobAnalyzeTime.beforeKaizenTime - (this.formData.jobAnalyzeTime.workingTime * this.formData.jobAnalyzeTime.workingDays * this.formData.jobAnalyzeTime.workingMonths)).toFixed(2);
    },
    jrPersonNumber() {
      return this.memo?.persons?.find(e => e.id == this.formData.jobRelated.mainPerson)?.personNumber;
    },
    jriPersonNumber() {
      return this.memo?.persons?.find(e => e.id == this.formData.jobRpaInfo.person)?.personNumber;
    },
    fileName() {
      return FnCommon.getFileNameFromPath(this.formData.jobRelated.attachment1);
    },
    fileName2() {
      return FnCommon.getFileNameFromPath(this.formData.jobRelated.attachment2);
    },
    fileName3() {
      return FnCommon.getFileNameFromPath(this.formData.jobRisk.attachment1);
    },
    fileName4() {
      return FnCommon.getFileNameFromPath(this.formData.jobRisk.attachment2);
    },
    disabledPaymentMethod() {
      if (this.formData.jobRisk) {
        return !this.formData.jobRisk.paymentMethodId;
      }
      return true;
    },
    manualFilePath1Value: {
      get(){
        return this.formData.jobRisk ? this.formData.jobRisk.manualFilePath1 : null;
      },
      set(newValue) {
        if (this.formData.jobRisk) {
          this.formData.jobRisk.manualFilePath1 = newValue;
        }
      }
    },
    manualFilePath2Value: {
      get(){
        return this.formData.jobRisk ? this.formData.jobRisk.manualFilePath2 : null;
      },
      set(newValue) {
        if (this.formData.jobRisk) {
          this.formData.jobRisk.manualFilePath2 = newValue;
        }
      }
    },
  },
  methods: {
    returnDetail() {
      this.$router.push({name: 'business-management-detail', params: {id: this.$route.params.id,backFromDetail: true}});
    },
    handleFile(file) {
      this.err = false;
      let formData = new FormData();
      formData.append("file", file[0]);
      RequestUtil.post(RPAApi.UPLOAD_FILE("rpa_spec"), formData).then(res => {
        if (res.status === 200) {
          this.uploadFile = file[0];
          this.formData.jobRelated.attachment1 = res.data.body;
          this.uploadFileDisable = true;
        } else {
          this.err = true;
        }
      })
    },
    deleteFile() {
      this.$confirm.msgBoxOk(RPAMessage.CONFIRM_DELETE).then(res => {
        if (res) {
          // RequestUtil.delete(RPAApi.DELETE_FILE("rpa_spec", this.formData.jobRelated.attachment1, 1));
          this.formData.jobRelated.pathAttachment1Del = this.formData.jobRelated.attachment1;
          this.formData.jobRelated.attachment1 = null;
          this.uploadFile = null;
          this.uploadFileDisable = false;
        }
      });
    },
    handleFile2(file) {
      this.err = false;
      let formData = new FormData();
      formData.append("file", file[0]);
      RequestUtil.post(RPAApi.UPLOAD_FILE("rpa_spec"), formData).then(res => {
        if (res.status === 200) {
          this.uploadFile = file[0];
          this.formData.jobRelated.attachment2 = res.data.body;
          this.uploadFile2Disable = true;
        } else {
          this.err = true;
        }
      })
    },
    deleteFile2() {
      this.$confirm.msgBoxOk(RPAMessage.CONFIRM_DELETE).then(res => {
        if (res) {
          // RequestUtil.delete(RPAApi.DELETE_FILE("rpa_spec", this.formData.jobRelated.attachment2, 2));
          this.formData.jobRelated.pathAttachment2Del = this.formData.jobRelated.attachment2;
          this.formData.jobRelated.attachment2 = null;
          this.uploadFile = null;
          this.uploadFile2Disable = false;
        }
      });
    },
    handleFile3(file) {
      this.err = false;
      let formData = new FormData();
      formData.append("file", file[0]);
      RequestUtil.post(RPAApi.UPLOAD_FILE("rpa_spec"), formData).then(res => {
        if (res.status === 200) {
          this.uploadFile = file[0];
          this.formData.jobRisk.attachment1 = res.data.body;
          this.uploadFile3Disable = true;
        } else {
          this.err = true;
        }
      })
    },
    deleteFile3() {
      this.$confirm.msgBoxOk(RPAMessage.CONFIRM_DELETE).then(res => {
        if (res) {
          // RequestUtil.delete(RPAApi.DELETE_FILE("rpa_spec", this.formData.jobRelated.attachment2, 2));
          this.formData.jobRisk.pathAttachment1Del = this.formData.jobRisk.attachment1;
          this.formData.jobRisk.attachment1 = null;
          this.uploadFile3Disable = false;
          this.uploadFile = null;
        }
      });
    },
    handleFile4(file) {
      this.err = false;
      let formData = new FormData();
      formData.append("file", file[0]);
      RequestUtil.post(RPAApi.UPLOAD_FILE("rpa_spec"), formData).then(res => {
        if (res.status === 200) {
          this.uploadFile = file[0];
          this.formData.jobRisk.attachment2 = res.data.body;
          this.uploadFile4Disable = true;
        } else {
          this.err = true;
        }
      })
    },
    deleteFile4() {
      this.$confirm.msgBoxOk(RPAMessage.CONFIRM_DELETE).then(res => {
        if (res) {
          // RequestUtil.delete(RPAApi.DELETE_FILE("rpa_spec", this.formData.jobRelated.attachment2, 2));
          this.formData.jobRisk.pathAttachment2Del = this.formData.jobRisk.attachment2;
          this.formData.jobRisk.attachment2 = null;
          this.uploadFile = null;
          this.uploadFile4Disable = false;
        }
      });
    },
    async checkRpaNumber() {
      this.coutCheckNumber = this.coutCheckNumber+1;
      if (this.coutCheckNumber > 1) {
        await this.sleep(1000);
        if (this.formData.rpaNumber == null || this.formData.rpaNumber == '') {
         return  this.rpaErrorMsg = false;
        }
        const result = await RequestUtil.get(RPAApi.EXIST_RPANUMBER(this.formData.rpaNumber));
         await this.sleep(1000);
        if (result.data.body == false) {
          this.rpaErrorMsg = true;
        }else {
          this.rpaErrorMsg = false;
        }
      }
    },

    async onSubmit() {
      if(this.formData.rpaNumber =='' || this.formData.rpaNumber == undefined){
          this.formData.rpaNumber = null;
        }
      if(this.formData.rpaNumber !== null) {
        this.checkRpaNumber();
      }
      if (this.formData.category?.trim() == ''){
        this.formData.category = null;
      }
      if (this.formData.jobRpaInfo.consignee?.trim() == ''){
        this.formData.jobRpaInfo.consignee = null;
      }
      const isValid = (await this.$refs.form.validate()).valid;
      if (!isValid || this.rpaErrorMsg == true) {
        window.scrollTo(0, 0);
        return;
      }
      
      axios
        .put(
          process.env.VUE_APP_API_URL +
            RPAApi.DETAIL_JOBS(this.$route.params.id),
          this.formData
        )
        .then((res) => {
          toast.success(RPAMessage.UPDATED_SUCCESS);
          this.returnDetail();
        })
        .catch((error) => {
          if (error.response.status === 409) {
            this.errorMessage = error.response.data.message;
            this.showErrorPopup = true;
          }else if(error.response.status === 404) {
            return this.$router.push({name:'login'});
          } else{
            toast.error("予期せぬエラーが発生しました。管理者にお問い合わせください。");
          }
        });
    },
    clearForm() {
      Object.keys(this.formData).forEach(key => {
        this.formData[key] = null;
      });
    },
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },
    checkRole(){
      
    },
    cancel() {
      this.showErrorPopup = false;
      this.returnDetail();
    },
    resetFormValues() {
      this.formData.jobRisk.manualFilePath1 = null;
      this.formData.jobRisk.manualFilePath2 = null;
      this.formData.jobRisk.riskLevel = null;
      this.formData.jobRisk.dataCreationId = null;
      this.formData.jobRisk.amountCategoryId = null;
      this.formData.jobRisk.attachment1 = null ;
      this.formData.jobRisk.attachment2 = null ;
    },
    getValidationRule(label) {
      if (!this.disabledPaymentMethod) {
        return `required:${label}`;
      }
      return '';
    }
  },
  watch: {
    "formData.rpaNumber" : function () {
      this.checkRpaNumber();
    },
    "formData.jobRisk.paymentMethodId": function(newPaymentMethodId, oldPaymentMethodId) {
      if (newPaymentMethodId === '') {
        this.resetFormValues();
      }
    },
  }
}
</script>

<style scoped>
.custom-file-label::after {
  display: none;
}

.custom-file-label {
  width: 35%;
}

.content-data {
  padding: 2px 16px;
}

.colorDisabled {
background-color: #e9ecef;
}
</style>