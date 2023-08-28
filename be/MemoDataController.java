package com.rpa.rpaui.controllers;

import com.rpa.rpaui.controllers.responses.ResponseData;
import com.rpa.rpaui.entities.*;
import com.rpa.rpaui.entities.types.*;
import com.rpa.rpaui.modelview.DepartmentView;
import com.rpa.rpaui.modelview.MPersonView;
import com.rpa.rpaui.repositories.*;
import com.rpa.rpaui.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/memo")
public class MemoDataController {
  @Autowired MSynchroidVersionService synchroidVersionService;
  @Autowired MDepartmentService departmentService;
  @Autowired MStatusService mStatusService;
  @Autowired MIntervalService mIntervalService;
  @Autowired MProvideFromService mProvideFromService;
  @Autowired MProvideToService mProvideToService;
  @Autowired MCategoriesService mCategoriesService;
  @Autowired MPersonsRepository personsRepository;
  @Autowired MConsignRepository consignRepository;
  @Autowired MConsigneeRepository consigneeRepository;
  @Autowired MDifficutieRepository difficutieRepository;
  @Autowired MManaualRepository mManaualRepository;
  @Autowired MProvideMethodRepository provideMethodRepository;
  @Autowired MProvideToDetailRepository provideToDetailRepository;
  @Autowired MJobStatuseService mJobStatuseService;
  @Autowired MPersonalizedService mPersonalizedService;
  @Autowired LedgerService ledgerService;
  @Autowired MJobIntervalRepositry mJobIntervalRepositry;
  @Autowired JobService jobService;
  @Autowired MLicenseTypeRepository mLicenseTypeRepository;
  @Autowired MRoleRepository mRoleRepository;
  @Autowired MDepartmentRepository departmentRepository;
  @Autowired MPersonService mPersonService;
  @Autowired MStatusRepository mStatusRepository;
  @Autowired MPositionRepository mPositionRepository;
  @Autowired MPersonStatusRepository mPersonStatusRepository;
  @Autowired MRiskAmountCategoryService amountCategoryService;
  @Autowired MRiskDataCreationService dataCreationService;
  @Autowired MRiskPaymentMethodService paymentMethodService;
  @Autowired
  PersonService personService;

  @GetMapping("synchroidVersion")
  public ResponseData<List<MSynchroidVersion>> getSynchroidVersion() {
    return new ResponseData<>(synchroidVersionService.getSynchroidVersion());
  }

  @GetMapping("intervals")
  public ResponseData<List<MInterval>> getIntervals() {
    return new ResponseData<>(mIntervalService.getIntervals());
  }

  @GetMapping("departments")
  public ResponseData<List<MDepartment>> getDepartMent() {
    return new ResponseData<>(departmentService.getAll());
  }

  @GetMapping("search_department")
  public ResponseData<List<DepartmentView>> departmentSearch() {
    List<MDepartment> lstPerson = departmentRepository.findAll();
    List<DepartmentView> lstDepartmentView = new ArrayList<>();
    for (MDepartment department : lstPerson) {
      DepartmentView departmentView = new DepartmentView();
      departmentView.text = department.getName();
      departmentView.value = department.getId();
      lstDepartmentView.add(departmentView);
    }
    return new ResponseData<>(lstDepartmentView);
  }

  @GetMapping("jobDepartment/{ledgerId}")
  public ResponseData<MDepartment> getLedgerJobDepartment(@PathVariable Integer ledgerId) {
    return new ResponseData<MDepartment>(departmentService.getFromJobByLedgerId(ledgerId));
  }

  @GetMapping("status")
  public ResponseData<List<MJobStatuse>> getMstatus() {
    return new ResponseData<>(mJobStatuseService.getAll());
  }

  @GetMapping("schedule")
  public ResponseData<Map> isScheduled() {
    return new ResponseData<>(ScheduleType.val);
  }

  @GetMapping("execTypes")
  public ResponseData<Map> execTypes() {
    return new ResponseData<>(ExecType.val);
  }

  @GetMapping("execDays")
  public ResponseData<Map> execDays() {
    return new ResponseData<>(ExecDayType.val);
  }

  @GetMapping("progress")
  public ResponseData<Map> progessType() {
    return new ResponseData<>(ProgressType.val);
  }

  @GetMapping("progressChangeFlg")
  public ResponseData<Map> progessChangeFlgType() {
    return new ResponseData<>(ProgressChangeFlgType.val);
  }

  @GetMapping("enhanceFlg")
  public ResponseData<Map> enhanceFlgType() {
    return new ResponseData<>(EnhanceFlgType.val);
  }

  @GetMapping("automationFlg")
  public ResponseData<Map> automationFlgType() {
    return new ResponseData<>(AutomationFlgType.val);
  }

  @GetMapping("jobInterval")
  public ResponseData<Map> jobIntervalType() {
    return new ResponseData<>(JobIntervalType.val);
  }

  @GetMapping("jobDiffType")
  public ResponseData<Map> jobDiffType() {
    return new ResponseData<>(JobDifficultyType.val);
  }

  @GetMapping("jobManual")
  public ResponseData<Map> jobManualType() {
    return new ResponseData<>(JobManualType.val);
  }

  @GetMapping("accessExtension")
  public ResponseData<Map> accessExtType() {
    return new ResponseData<>(AccessExtensionType.val);
  }

  @GetMapping("hearingFlg")
  public ResponseData<Map> hearingFlgType() {
    return new ResponseData<>(HearingFlgType.val);
  }

  @GetMapping("rpaPriority")
  public ResponseData<Map> rpaPriorityType() {
    return new ResponseData<>(RpaPriorityType.val);
  }

  @GetMapping("accessExcel")
  public ResponseData<Map> accessExcelType() {
    return new ResponseData<>(AccessExcelType.val);
  }

  @GetMapping("userReview")
  public ResponseData<Map> userReviewType() {
    return new ResponseData<>(UserReviewType.val);
  }

  @GetMapping("provideFrom")
  public ResponseData<List<MProvideFrom>> getNameProvideFrom() {
    return new ResponseData<>(mProvideFromService.getNameProvideFrom());
  }

  @GetMapping("provideTo")
  public ResponseData<List<MProvideTo>> getNameProvideTo() {
    return new ResponseData<>(mProvideToService.getNameProvideTo());
  }

  @GetMapping("provideToDetail")
  public ResponseData<List<MProvideToDetail>> getProvideToDetail() {
    return new ResponseData<>(provideToDetailRepository.findAll());
  }

//  @GetMapping("mainPerson")
//  public ResponseData<List<MProvideTo>> getMainPerson() {
//    return new ResponseData<>(mProvideToService.getNameProvideTo());
//  }

  @GetMapping("provideMethod")
  public ResponseData<List<MProvideMethod>> getProvideMethod() {
    return new ResponseData<>(provideMethodRepository.findAll());
  }

  @GetMapping("personalizeds")
  public ResponseData<List<MPersonalized>> getPersonalizeds() {
    return new ResponseData<>(mPersonalizedService.getNamePersonalized());
  }

  @GetMapping("formats")
  public ResponseData<Map> getFormats() {
    return new ResponseData<>(FormatType.val);
  }

  @GetMapping("manuals")
  public ResponseData<List<MManual>> getManuals() {
    return new ResponseData<>(mManaualRepository.findAll());
  }

  @GetMapping("difficulties")
  public ResponseData<List<MDifficultie>> difficulties() {
    return new ResponseData<>(difficutieRepository.findAll());
  }

  @GetMapping("consignees")
  public ResponseData<List<MConsignee>> consignees() {
    return new ResponseData<>(consigneeRepository.findAll());
  }

  @GetMapping("consigns")
  public ResponseData<List<MConsign>> consigns() {
    return new ResponseData<>(consignRepository.findAll());
  }

  @GetMapping("persons")
  public ResponseData<List<MPerson>> persons() {
    return new ResponseData<>(personsRepository.findAll());
  }

  @GetMapping("search_persons")
  public ResponseData<List<MPersonView>> personSearch() {
    List<MPerson> lstPerson = personsRepository.findAll();
    List<MPersonView> lstPersonView = new ArrayList<>();
    for (MPerson person : lstPerson) {
      MPersonView personView = new MPersonView();
      personView.text = person.getPersonName();
      personView.value = person.getId();
      lstPersonView.add(personView);
    }
    return new ResponseData<>(lstPersonView);
  }

  @GetMapping("categories")
  public ResponseData<List<MCategory>> getCategory() {
    return new ResponseData<>(mCategoriesService.getAll());
  }

  @GetMapping("ledger")
  public ResponseData<List<Ledger>> getLedger(){
   return new ResponseData<>(ledgerService.getLedgerListOption());
  }

  @GetMapping("jobintervals")
  public ResponseData<List<MJobInterval>> getJobIntervals() {
    return new ResponseData<>(mJobIntervalRepositry.findAll());
  }

  @GetMapping("jobnumber")
  public ResponseData<String> getJobNumber() {
    return new ResponseData<>(jobService.generateJobNumber());
  }

  @GetMapping("checkRPANo/{rpaNumber}")
  public ResponseData<Boolean> checkExistRPANumber(@PathVariable String rpaNumber) {
    Ledger ledger = ledgerService.findLedgerByRpaNumber(rpaNumber);
    if (Objects.nonNull(ledger)) {
      return new ResponseData<>(true);
    }
    return new ResponseData<>(false);
  }

  @GetMapping("licenseTypes")
  public ResponseData<List<MLicenseTypes>> getlicenseTypes() {
    return new ResponseData<>(mLicenseTypeRepository.findAll());
  }

  @GetMapping("scheduleStatus")
  public ResponseData<Map> scheduleStatus() {
    return new ResponseData<>(ScheduleStatus.val);
  }

  @GetMapping("scheduleAutoStatus")
  public ResponseData<Map> scheduleAutoStatus() {
    return new ResponseData<>(ScheduleStatus.autoStatus);
  }

  @GetMapping("robotStatus")
  public ResponseData<List<MStatus>> getRobotStatus() {
    return new ResponseData<>(mStatusService.getAll());
  }

  @GetMapping("roles")
  public ResponseData<List<MRole>> getRole() {
    return new ResponseData<>(mRoleRepository.findAll());
  }

  @GetMapping("mposition")
  public ResponseData<List<MPosition>> getPosition() {
    return new ResponseData<>(mPositionRepository.findAll());
  }

  @GetMapping("personStatus")
  public ResponseData<List<MPersonStatus>> getpersonStatus() {
    return new ResponseData<>(mPersonStatusRepository.findAll());
  }

  @GetMapping("departmentByUser")
  public ResponseData<Integer> getDepartmentByUser(){
    MPerson person = personService.getDetailPerson();
    Integer departmentByUser = null;
    if(Objects.nonNull(person.getMDepartment())){
      departmentByUser = person.getMDepartment().getId();
    }
    return new ResponseData<Integer>(departmentByUser);
  }

  @GetMapping("search_persons_status")
  public ResponseData<List<MPersonView>> personSearchStatus() {
    List<MPerson> lstPerson = personsRepository.findAll();
    List<MPersonView> lstPersonView = new ArrayList<>();
    Integer status;
    for (MPerson person : lstPerson) {
      MPersonView personView = new MPersonView();
      personView.text = person.getPersonName();
      personView.value = person.getId();
      if(person.getMPersonStatus() == null){
        status = 0;
      } else {
        status = person.getMPersonStatus().getId();
      }
      if(status != 2 ){
        lstPersonView.add(personView);
      }

    }
    return new ResponseData<>(lstPersonView);
  }

  @GetMapping("amount_categories")
  public ResponseData<List<MRiskAmountCategory>> getAmountCategory() {
    return new ResponseData<>(amountCategoryService.getAll());
  }

  @GetMapping("data_creations")
  public ResponseData<List<MRiskDataCreation>> getDataCreation() {
    return new ResponseData<>(dataCreationService.getAll());
  }

  @GetMapping("payment_methods")
  public ResponseData<List<MRiskPaymentMethod>> getPaymentMethod() {
    return new ResponseData<>(paymentMethodService.getAll());
  }
}
