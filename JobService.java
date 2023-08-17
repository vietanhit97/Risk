package com.rpa.rpaui.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpa.rpaui.controllers.dtos.JobListDto;
import com.rpa.rpaui.entities.*;
import com.rpa.rpaui.exceptions.ExceptionRpa;
import com.rpa.rpaui.modelview.JobView;
import com.rpa.rpaui.repositories.*;
import com.rpa.rpaui.utils.FnCommon;
import com.rpa.rpaui.utils.RpaConstant;
import com.rpa.rpaui.utils.RpaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {
  protected static final Logger LOG = LoggerFactory.getLogger(JobService.class);

  @Autowired JobRepository jobRepository;
  @Autowired MDepartmentRepository departmentRepository;
  @Autowired MCategoriesRepository mCategoriesRepository;
  @Autowired MProvideFromRepository mProvideFromRepository;
  @Autowired MProvideToRepository mProvideToRepository;
  @Autowired JobRpaInfoRepository jobRpaInfoRepository;
  @Autowired JobAnalyzeTimeRepository jobAnalyzeTimeRepository;
  @Autowired JobRelatedRepository jobRelatedRepository;
  @Autowired BaseCustomRepository baseCustomRepository;
  @Autowired MPersonsRepository mPersonsRepository;
  @Autowired MConsigneeRepository mConsigneeRepository;
  @Autowired MConsignRepository mConsignRepository;
  @Autowired MJobStatuseRepository mJobStatuseRepository;
  @Autowired MProvideMethodRepository mProvideMethodRepository;
  @Autowired MPersonalizedRepository mPersonalizedRepository;
  @Autowired MProvideToDetailRepository mProvideToDetailRepository;
  @Autowired MJobIntervalRepositry mJobIntervalRepositry;
  @Autowired LedgerService ledgerService;
  @Autowired ExecDateRepository execDateRepository;
  @Autowired ExecDayRepository execDayRepository;
  @Autowired SchedulesRepository schedulesRepository;
  @Autowired XmlScheduleRepository xmlScheduleRepository;
  @Autowired MStatusRepository mStatusRepository;
  @Autowired LedgersLinkRepository ledgersLinkRepository;
  @Autowired LedgersLinkService ledgersLinkService;
  @Autowired FileStorageService fileStorageService;
  @Autowired MDepartmentRepository mDepartmentRepository;
  @Autowired MPersonsRepository personsRepository;
  @Autowired AuthService authService;
  @Autowired PersonService personService;
  @Autowired MRiskAmountCategoryRepository amountCategoryRepository;
  @Autowired MRiskPaymentMethodRepository paymentMethodRepository;
  @Autowired MRiskDataCreationRepository dataCreationRepository;
  @Autowired JobRiskRepository jobRiskRepository;

  public Page<JobListDto> getJobs(HttpServletRequest request, Pageable pageable) {
    try {
      StringBuilder sql = new StringBuilder();
      StringBuilder setRoleSql = new StringBuilder();
      Integer role = authService.getRoles();
      if(role == 3 ) {
        setRoleSql.append(" and m_departments.dept_role_flg != 1 ");
      }
      sql.append(
          "select * from ("
              + "select "
              + "jobs.id, jobs.number, jobs.name, ledgers.id as ledgerId, ledgers.rpa_number as rpaNumber, m_departments.name as departmentName, jobs.category, job_risks.risk_level as jobRisk, "
              + "m_persons.person_name as personName,  job_rpa_infos.consignee as consigneeName, m_job_statuses.name as jobStatusesName, TO_CHAR(jobs.updated_at, 'YYYY-MM-DD HH24:MI:SS') as updatedAt "
              + "from jobs "
              + "left join ledgers_link on ledgers_link.job_id = jobs.id and ledgers_link.is_deleted=:isNotDelete "
              + "left join ledgers on ledgers.id = ledgers_link.ledger_id and ledgers.is_deleted=:isNotDelete "
              + "left join m_departments on m_departments.id = jobs.department "
              + "left join job_rpa_infos on job_rpa_infos.job_id = jobs.id and job_rpa_infos.is_deleted=:isNotDelete "
              + "left join m_persons on m_persons.id = job_rpa_infos.person and job_rpa_infos.is_deleted=:isNotDelete "
              + "left join job_risks on job_risks.job_id = jobs.id and job_risks.is_deleted=:isNotDelete "
              // + "left join m_consignees on m_consignees.id = job_rpa_infos.consignee and
              // m_consignees.is_deleted=:isNotDelete "
              + "left join m_job_statuses on m_job_statuses.id = jobs.status and m_job_statuses.is_deleted=:isNotDelete "
              + "where jobs.is_deleted = :isNotDelete "
              + setRoleSql
              + ") as job where 1=1");

      
      String filterFields = request.getParameter("filterFields");
      if(Objects.nonNull(filterFields)) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(filterFields, Map.class);

        for (String key : map.keySet()) {
          String filterSql = getFilterSql(key.toString(), map.get(key));
          sql.append(filterSql);
        }
      }

      String sortBy = Objects.nonNull(request.getParameter("sortBy")) ? request.getParameter("sortBy") : "id";
      String order = Objects.nonNull(request.getParameter("order")) ? request.getParameter("order") : "desc";

      sql.append(" order by ")
          .append(sortBy)
          .append(" ")
          .append(order);

      HashMap<String, Object> params = new HashMap<>();
      params.put("isNotDelete", RpaConstant.IS_NOT_DELETE);

      int page = Objects.nonNull(request.getParameter("page")) ? Integer.valueOf(request.getParameter("page")) : 1;
      int size = Objects.nonNull(request.getParameter("size")) ? Integer.valueOf(request.getParameter("size")) : 100;
      pageable = PageRequest.of(page - 1, size);
      // System.out.println("sql: " + sql);
      return (Page<JobListDto>)
          baseCustomRepository.getPageData(
              sql, params, pageable, JobListDto.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new PageImpl<>(new ArrayList<>(), pageable, 0);
  }

  private String getFilterSql(String key, String filterValues) {
    StringBuilder sql = new StringBuilder();
    if (key != null && filterValues != null && filterValues.length() != 0) {
      String[] values = filterValues.split(",");
      boolean hasBlank = Arrays.asList(values).contains(RpaConstant.BLANK_VALUE);
      boolean isSelectedAll = Arrays.asList(values).contains(RpaConstant.SELECT_ALL);
      String valueAsStr =
          Arrays.stream(values)
              .filter(s -> !s.equals(RpaConstant.BLANK_VALUE) && !s.equals(RpaConstant.SELECT_ALL))
              .collect(Collectors.joining("','", "'", "'"));
      if (isSelectedAll) {
        if (values.length == 2 && hasBlank) {
          sql.append(" and (job.").append(key).append(" is not null )");
        } else if (values.length > 2 && hasBlank) {
          sql.append(" and (job.")
              .append(key)
              .append(" not in (")
              .append(valueAsStr)
              .append(")")
              .append(" and job.")
              .append(key)
              .append(" is not null ")
              .append(")");
        } else if (values.length >= 2) {
          sql.append(" and (job.")
              .append(key)
              .append(" not in (")
              .append(valueAsStr)
              .append(")")
              .append(" or job.")
              .append(key)
              .append(" is null ")
              .append(")");
        }
      } else {
        if (values.length == 1 && hasBlank) {
          sql.append(" and (job.").append(key).append(" is null )");
        } else if (values.length > 1 && hasBlank) {
          sql.append(" and (job.")
              .append(key)
              .append(" in (")
              .append(valueAsStr)
              .append(")")
              .append(" or job.")
              .append(key)
              .append(" is null ")
              .append(")");
        } else if (values.length >= 1) {
          sql.append(" and job.")
              .append(key)
              .append(" in (")
              .append(valueAsStr)
              .append(")");
        }
      }
    }

    return sql.toString();
  }

  public Job getById(int id) {
    return jobRepository
        .findById(id)
        .orElseThrow(() -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_NOT_FOUND, id));
  }

  public Job getByLedgerLinkId(int ledger_link_id) {
    Job job = jobRepository.findJobByLedgerLinkId(ledger_link_id);
    return job;
  }

  @Transactional
  public Job createJob(JobView dto) {

    String createUser = personService.getDetailPerson().getPersonNumber();
    LocalDateTime updateAt = LocalDateTime.now();

    Job job = new Job();
    JobRpaInfo jobRpaInfo = new JobRpaInfo();
    JobAnalyzeTime jobAnalyzeTime = new JobAnalyzeTime();
    JobRelated jobRelated = new JobRelated();
    LedgersLink ledgersLink = new LedgersLink();
    JobRisk jobRisks = new JobRisk();

    FnCommon.coppyNonNullProperties(job, dto);

    if (Objects.nonNull(dto.getDepartment())) {
      MDepartment department =
          departmentRepository
              .findById(dto.getDepartment())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.DEPARTMENT_NOT_FOUND,
                          dto.getDepartment()));
      job.setMdepartment(department);
    }
    if (Objects.nonNull(dto.getCategory())) {
      job.setCategory(dto.getCategory().trim());
    }
    if (Objects.nonNull(dto.getStatus())) {
      MJobStatuse mJobStatuse =
          mJobStatuseRepository
              .findById(dto.getStatus())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND, RpaMessage.MJOBSTATUSE_NOT_FOUND, dto.getStatus()));
      job.setMJobStatuse(mJobStatuse);
    }
    
    job.setLedgerId(null);

    // -----------------------JobRpaInfo table
    jobRpaInfo.setJob(job);
    if (Objects.nonNull(dto.getJobRpaInfo().getPerson())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRpaInfo().getPerson())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRpaInfo().getPerson()));
      jobRpaInfo.setMPerson(mPerson);
    }
    if (Objects.nonNull(dto.getJobRpaInfo().getConsignFlag())) {
      MConsign mConsign =
          mConsignRepository
              .findById(dto.getJobRpaInfo().getConsignFlag())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MCONSIGN_NOT_FOUND,
                          dto.getJobRpaInfo().getConsignFlag()));
      jobRpaInfo.setMConsign(mConsign);
    }
    if (Objects.nonNull(dto.getJobRpaInfo().getConsignee())) {
      jobRpaInfo.setConsignee(dto.getJobRpaInfo().getConsignee());
    }

    jobAnalyzeTime.setJob(job);
    jobRelated.setJob(job);
    jobRisks.setJob(job);
    FnCommon.coppyNonNullProperties(jobRpaInfo, dto);
    FnCommon.coppyNonNullProperties(jobAnalyzeTime, dto.jobAnalyzeTime);
    FnCommon.coppyNonNullProperties(jobRelated, dto.jobRelated);
    FnCommon.coppyNonNullProperties(jobRisks, dto.jobRisk);
    
    // -----------------------JobRelated table
    if (Objects.nonNull(dto.getJobRelated().getProvideFrom())) {
      MProvideFrom provideFrom =
          mProvideFromRepository
              .findById(dto.getJobRelated().getProvideFrom())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.PROVIDE_FROM_NOT_FOUND,
                          dto.getJobRelated().getProvideFrom()));
      jobRelated.setMProvideFrom(provideFrom);
    }
    if (Objects.nonNull(dto.getJobRelated().getProvideTo())) {
      MProvideTo provideTo =
          mProvideToRepository
              .findById(dto.getJobRelated().getProvideTo())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.PROVIDE_FROM_NOT_FOUND,
                          dto.getJobRelated().getProvideFrom()));
      jobRelated.setMProvideTo(provideTo);
    }

    if (Objects.nonNull(dto.getJobRelated().getProvideMethod())) {
      MProvideMethod provideMethod =
          mProvideMethodRepository
              .findById(dto.getJobRelated().getProvideMethod())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPROVIDE_METHOD,
                          dto.getJobRelated().getProvideMethod()));
      jobRelated.setMProvideMethod(provideMethod);
    }
    if (Objects.nonNull(dto.getJobRelated().getPersonalized())) {
      MPersonalized mPersonalized =
          mPersonalizedRepository
              .findById(dto.getJobRelated().getPersonalized())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSONALIZEDS,
                          dto.getJobRelated().getPersonalized()));
      jobRelated.setMPersonalized(mPersonalized);
    }
    if (Objects.nonNull(dto.getJobRelated().getProvideToDetail())) {
      MProvideToDetail mProvideToDetail =
          mProvideToDetailRepository
              .findById(dto.getJobRelated().getProvideToDetail())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPROVIDE_TO_DETAIL,
                          dto.getJobRelated().getProvideToDetail()));
      jobRelated.setMProvideToDetail(mProvideToDetail);
    }
    if (Objects.nonNull(dto.getJobRelated().getMainPerson())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getMainPerson())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getMainPerson()));
      jobRelated.setMPerson(mPerson);
    }
    if (Objects.nonNull(dto.getJobRelated().getSubPerson1())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getSubPerson1())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getSubPerson1()));
      jobRelated.setSubPerson1(mPerson);
    }
    if (Objects.nonNull(dto.getJobRelated().getSubPerson2())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getSubPerson2())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getSubPerson2()));
      jobRelated.setSubPerson2(mPerson);
    }
    if (Objects.nonNull(dto.getJobRelated().getSubPerson3())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getSubPerson3())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getSubPerson3()));
      jobRelated.setSubPerson3(mPerson);
    }
    // -----------------------JobRisks table
    if (Objects.nonNull(dto.getJobRisk().getAmountCategoryId())) {
      MRiskAmountCategory amountCategory = amountCategoryRepository
          .findById(dto.getJobRisk().getAmountCategoryId())
          .orElseThrow(
              () -> new ExceptionRpa(
                  HttpStatus.NOT_FOUND,
                  RpaMessage.AMOUNTCATEGORY_NOT_FOUND,
                  dto.getJobRelated().getSubPerson3()));
      jobRisks.setMRiskAmountCategory(amountCategory);
    }
    if (Objects.nonNull(dto.getJobRisk().getDataCreationId())) {
      MRiskDataCreation dataCreation = dataCreationRepository
          .findById(dto.getJobRisk().getDataCreationId())
          .orElseThrow(
              () -> new ExceptionRpa(
                  HttpStatus.NOT_FOUND,
                  RpaMessage.DATACREATION_NOT_FOUND,
                  dto.getJobRelated().getSubPerson3()));
      jobRisks.setMRiskDataCreation(dataCreation);
    }
    if (Objects.nonNull(dto.getJobRisk().getPaymentMethodId())) {
      MRiskPaymentMethod paymentMethod = paymentMethodRepository
          .findById(dto.getJobRisk().getPaymentMethodId())
          .orElseThrow(
              () -> new ExceptionRpa(
                  HttpStatus.NOT_FOUND,
                  RpaMessage.PAYMENTMETHOD_NOT_FOUND,
                  dto.getJobRelated().getSubPerson3()));
      jobRisks.setMRiskPaymentMethod(paymentMethod);
    }
    // -----------------------JobAnalyzeTime table
    if (Objects.nonNull(dto.getJobAnalyzeTime().getJobInterval())) {
      MJobInterval mJobInterval =
          mJobIntervalRepositry
              .findById(dto.getJobAnalyzeTime().getJobInterval())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MJOBINTERVAL_NOT_FOUND,
                          dto.getJobAnalyzeTime().getJobInterval()));
      jobAnalyzeTime.setMJobInterval(mJobInterval);
    }

    if (Objects.nonNull(dto.getJobAnalyzeTime().getWorkingTime())) {
      Integer workingTimeRound = Math.round(dto.getJobAnalyzeTime().getWorkingTime() * 60);
      jobAnalyzeTime.setWorkingTime(workingTimeRound);
    }
    if (Objects.nonNull(dto.getJobAnalyzeTime().getWorkingDays())) {
      Integer workingDayRound = Math.round(dto.getJobAnalyzeTime().getWorkingDays() * 24);
      jobAnalyzeTime.setWorkingDays(workingDayRound);
    }

    if (Objects.nonNull(dto.getJobAnalyzeTime().getBeforeKaizenTime())) {
      Integer bfKaizenTimeRound = Math.round(dto.getJobAnalyzeTime().getBeforeKaizenTime() * 60);
      if (!bfKaizenTimeRound.equals(jobAnalyzeTime.getBeforeKaizenTime())) {
        jobAnalyzeTime.setBeforeKaizenTime(bfKaizenTimeRound);
      }
    }

    if (Objects.nonNull(dto.getJobAnalyzeTime().getBeforeKaizenTime())
        && Objects.nonNull(dto.getJobAnalyzeTime().getWorkingDays())
        && Objects.nonNull(dto.getJobAnalyzeTime().getWorkingTime())
        && Objects.nonNull(dto.getJobAnalyzeTime().getWorkingMonths())) {
      jobAnalyzeTime.setLastKaizenDate(FnCommon.getSysdate("yyyy-MM-dd HH:mm:ss"));
    }

    job.setCreatedUser(createUser);
    job.setUpdatedUser(createUser);    
    job.setUpdatedAt(updateAt);
    if (Objects.nonNull(dto.rpaNumber)) {
      Ledger ledger = null;
      ledger = ledgerService.findLedgerByRpaNumber(dto.rpaNumber);
      if (ledger == null) {
        throw new ExceptionRpa(
            HttpStatus.NOT_FOUND, RpaMessage.LEDGER_NOT_FOUND, dto.getJobRelated().getSubPerson1());
      }
      ledgersLink.setLedger(ledger);
      ledgersLink.setJob(job);
      ledgersLink.setUpdatedUser(createUser);
      ledgersLink.setCreatedUser(createUser);
      ledgersLinkRepository.save(ledgersLink);
    }

    job.setNumber(generateJobNumber());
    
    jobRepository.save(job);

    if(jobRpaInfo != null) {
      jobRpaInfo.setCreatedUser(createUser);
      jobRpaInfo.setUpdatedUser(createUser); 
    }
    if(jobAnalyzeTime != null){
      jobAnalyzeTime.setCreatedUser(createUser);
      jobAnalyzeTime.setUpdatedUser(createUser); 
    }

    if(jobRelated != null) {
      jobRelated.setCreatedUser(createUser);
      jobRelated.setUpdatedUser(createUser); 
    }

    if (jobRisks != null) {
      jobRisks.setCreatedUser(createUser);
      jobRisks.setUpdatedUser(createUser);
      jobRisks.setRiskLevelJob(dto.getJobRisk().getRiskLevelJob());
    }

    jobRpaInfoRepository.save(jobRpaInfo);
    jobAnalyzeTimeRepository.save(jobAnalyzeTime);
    jobRelatedRepository.save(jobRelated);
    jobRiskRepository.save(jobRisks);
    return job;
  }

  // Update Job
  @Transactional
  public Job updateJob(JobView dto, int id) {
    String updatedUser = personService.getDetailPerson().getPersonNumber();
    LocalDateTime updateAt = LocalDateTime.now();
    Boolean updateRpaInfoFlg = false;
    Boolean updateRpaRelatedFlg = false;
    Boolean updateRpaAnalyzeFlg = false;
    Boolean updateJobFlg = false;
    Boolean updateRpaRiskFlg = false;

    Job job =
        jobRepository
            .findById(id)
            .orElseThrow(
                () -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_NOT_FOUND, id));
    
    //Check optimistic lock
    if(job.getVersion() != dto.getVersion()){
      throw new ExceptionRpa(HttpStatus.CONFLICT, RpaMessage.PERSON_OPTIMISTIC_LOCK);
    }

    if (Objects.nonNull(dto.getStatus())) {
      MJobStatuse mJobStatuse =
          mJobStatuseRepository
              .findById(dto.getStatus())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND, RpaMessage.MJOBSTATUSE_NOT_FOUND, dto.getStatus()));
        if(Objects.nonNull(job.getMJobStatuse())){
          if(!dto.getStatus().equals(job.getMJobStatuse().getId())){
            updateJobFlg = true;
          }
        } else {
          updateJobFlg = true;
        }
      job.setMJobStatuse(mJobStatuse);
    } else {
      throw new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.MJOBSTATUSE_NOT_FOUND, dto.getStatus());
    }
    
    if (Objects.nonNull(dto.getDepartment())) {
      MDepartment department =
          departmentRepository
              .findById(dto.getDepartment())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.DEPARTMENT_NOT_FOUND,
                          dto.getDepartment()));
        if(Objects.nonNull(job.getMdepartment())){
          if(!dto.getDepartment().equals(job.getMdepartment().getId()) ) {
            updateJobFlg = true;
          }
        } else {
          updateJobFlg = true;
        }
      job.setMdepartment(department);
    } else {
      if(Objects.nonNull(job.getMdepartment())) {
        updateJobFlg = true;
      }
      job.setMdepartment(null);
    }
    job.setCategory(Objects.nonNull(dto.getCategory()) ? dto.getCategory().trim() : null);

    // =========== job_rpa_info
    JobRpaInfo jobRpaInfo =
        jobRpaInfoRepository
            .findById(job.getJobRpaInfo().getId())
            .orElseThrow(
                () -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_NOT_FOUND, id));

    if (Objects.nonNull(dto.getJobRpaInfo().getPerson())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRpaInfo().getPerson())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRpaInfo().getPerson()));

      if(job.getJobRpaInfo().getMPerson() != null){
        if(!dto.getJobRpaInfo().getPerson().equals(job.getJobRpaInfo().getMPerson().getId()) ){
          updateRpaInfoFlg = true;
        }
      } else {
        updateRpaInfoFlg = true;
      }
      jobRpaInfo.setMPerson(mPerson);
    } else {
      if(job.getJobRpaInfo().getMPerson() != null){
        updateRpaInfoFlg = true;
        jobRpaInfo.setMPerson(null);
      }
    }

    if (Objects.nonNull(dto.getJobRpaInfo().getConsignFlag())) {
      MConsign mConsign =
          mConsignRepository
              .findById(dto.getJobRpaInfo().getConsignFlag())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MCONSIGN_NOT_FOUND,
                          dto.getJobRpaInfo().getConsignFlag()));
      if(job.getJobRpaInfo().getMConsign() != null){
        if(dto.getJobRpaInfo().getConsignFlag() != job.getJobRpaInfo().getMConsign().getId()){
          updateRpaInfoFlg = true;
        }
      } else {
        updateRpaInfoFlg = true;
      }
      jobRpaInfo.setMConsign(mConsign);
    } else {
      if(job.getJobRpaInfo().getMConsign() != null){
        updateRpaInfoFlg = true;
        jobRpaInfo.setMConsign(null);
      }
    }

    if (Objects.nonNull(dto.getJobRpaInfo().getConsignee())) {
      MConsignee mConsignee = 
          mConsigneeRepository
              .findByName(dto.getJobRpaInfo().getConsignee())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MCONSIGN_NOT_FOUND,
                          dto.getJobRpaInfo().getConsignFlag()));
      if(job.getJobRpaInfo().getConsignee() != null){
        if(!dto.getJobRpaInfo().getConsignee().equals(job.getJobRpaInfo().getConsignee())){
          updateRpaInfoFlg = true;
        }
      } else {
        updateRpaInfoFlg = true;
      }
      jobRpaInfo.setConsignee(mConsignee.getName());              
    } else {
      if(job.getJobRpaInfo().getConsignee() != null){
        updateRpaInfoFlg = true;
        jobRpaInfo.setConsignee(null);
      }
    }
    
    // ============== job_analyze_time
    Boolean check = false;
    JobAnalyzeTime jobAnalyzeTime =
        jobAnalyzeTimeRepository
            .findById(job.getJobAnalyzeTime().getId())
            .orElseThrow(
                () -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_NOT_FOUND, id));

    Integer workingMonthDB = jobAnalyzeTime.getWorkingMonths();
    if (dto.getJobAnalyzeTime().getWorkingTime() != null
        && jobAnalyzeTime.getWorkingTime() != null
        && check == false) {
      if (jobAnalyzeTime.getWorkingTime()
          != (Math.round(dto.getJobAnalyzeTime().getWorkingTime() * 60))) {
        jobAnalyzeTime.setLastKaizenDate(FnCommon.getSysdate("yyyy-MM-dd HH:mm:ss"));
        updateRpaAnalyzeFlg = true;
        check = true;
      }
    }
    if (dto.getJobAnalyzeTime().getWorkingMonths() != null
        && workingMonthDB != null
        && check == false) {
      if (!workingMonthDB.equals(dto.getJobAnalyzeTime().getWorkingMonths())) {
        jobAnalyzeTime.setLastKaizenDate(FnCommon.getSysdate("yyyy-MM-dd HH:mm:ss"));
        updateRpaAnalyzeFlg = true;
        check = true;
      }
    }
    if (dto.getJobAnalyzeTime().getWorkingDays() != null
        && jobAnalyzeTime.getWorkingDays() != null
        && check == false) {
      if (jobAnalyzeTime.getWorkingDays()
          != (Math.round(dto.getJobAnalyzeTime().getWorkingDays() * 24))) {
        jobAnalyzeTime.setLastKaizenDate(FnCommon.getSysdate("yyyy-MM-dd HH:mm:ss"));
        updateRpaAnalyzeFlg = true;
        check = true;
      }
    }
    if (dto.getJobAnalyzeTime().getBeforeKaizenTime() != null
        && jobAnalyzeTime.getBeforeKaizenTime() != null
        && check == false) {
      if (jobAnalyzeTime.getBeforeKaizenTime()
          != (Math.round(dto.getJobAnalyzeTime().getBeforeKaizenTime() * 60))) {
        jobAnalyzeTime.setLastKaizenDate(FnCommon.getSysdate("yyyy-MM-dd HH:mm:ss"));
        check = true;
        updateRpaAnalyzeFlg = true;
      }
    }
    if (check == false) {
      if (((dto.getJobAnalyzeTime().getWorkingTime() == null
                  && jobAnalyzeTime.getWorkingTime() != null)
              || (dto.getJobAnalyzeTime().getWorkingTime() != null
                  && jobAnalyzeTime.getWorkingTime() == null))
          || ((dto.getJobAnalyzeTime().getWorkingMonths() == null && workingMonthDB != null)
              || (dto.getJobAnalyzeTime().getWorkingMonths() != null && workingMonthDB == null))
          || ((dto.getJobAnalyzeTime().getWorkingDays() == null
                  && jobAnalyzeTime.getWorkingDays() != null)
              || (dto.getJobAnalyzeTime().getWorkingDays() != null
                  && jobAnalyzeTime.getWorkingDays() == null))
          || ((dto.getJobAnalyzeTime().getBeforeKaizenTime() == null
                  && jobAnalyzeTime.getBeforeKaizenTime() != null)
              || (dto.getJobAnalyzeTime().getBeforeKaizenTime() != null
                  && jobAnalyzeTime.getBeforeKaizenTime() == null))) {
        jobAnalyzeTime.setLastKaizenDate(FnCommon.getSysdate("yyyy-MM-dd HH:mm:ss"));
        updateRpaAnalyzeFlg = true;
      }
    }
    if (Objects.nonNull(dto.getJobAnalyzeTime().getJobInterval())) {
      MJobInterval mJobInterval =
          mJobIntervalRepositry
              .findById(dto.getJobAnalyzeTime().getJobInterval())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MJOBINTERVAL_NOT_FOUND,
                          dto.getJobAnalyzeTime().getJobInterval()));
      jobAnalyzeTime.setMJobInterval(mJobInterval);
      if(dto.getJobAnalyzeTime().getJobInterval() != job.getJobAnalyzeTime().getMJobInterval().getId()) {
        updateRpaAnalyzeFlg = true;
      }
    } else {
      jobAnalyzeTime.setMJobInterval(null);
      if(job.getJobAnalyzeTime().getMJobInterval() != null) {
        updateRpaAnalyzeFlg = true;
      }
    }
    if (Objects.nonNull(dto.getJobAnalyzeTime().getWorkingTime())) {
      Integer workingTimeRound = Math.round(dto.getJobAnalyzeTime().getWorkingTime() * 60);
      if (!workingTimeRound.equals(jobAnalyzeTime.getWorkingTime())) {
        jobAnalyzeTime.setWorkingTime(workingTimeRound);
        updateRpaAnalyzeFlg = true;
      }
    } else {
      jobAnalyzeTime.setWorkingTime(null);
      if(job.getJobAnalyzeTime().getWorkingTime() != null) {
        updateRpaAnalyzeFlg = true;
      }
    }
    if (Objects.nonNull(dto.getJobAnalyzeTime().getWorkingDays())) {
      Integer workingDayRound = Math.round(dto.getJobAnalyzeTime().getWorkingDays() * 24);
      if (!workingDayRound.equals(jobAnalyzeTime.getWorkingDays())) {
        jobAnalyzeTime.setWorkingDays(workingDayRound);
        updateRpaAnalyzeFlg = true;

      }
    } else {
      jobAnalyzeTime.setWorkingDays(null);
      if(job.getJobAnalyzeTime().getWorkingDays() != null) {
        updateRpaAnalyzeFlg = true;
      }
    }

    if (Objects.nonNull(dto.getJobAnalyzeTime().getBeforeKaizenTime())) {
      Integer bfKaizenTimeRound = Math.round(dto.getJobAnalyzeTime().getBeforeKaizenTime() * 60);
      if (!bfKaizenTimeRound.equals(jobAnalyzeTime.getBeforeKaizenTime())) {
        jobAnalyzeTime.setBeforeKaizenTime(bfKaizenTimeRound);
        updateRpaAnalyzeFlg = true;
      }
    } else {
      jobAnalyzeTime.setBeforeKaizenTime(null);
      if(job.getJobAnalyzeTime().getBeforeKaizenTime() != null) {
        jobAnalyzeTime.setUpdatedUser(updatedUser);
        updateRpaAnalyzeFlg = true;
      }
    }

    // ============== job_related

    if(dto.getJobRelated().getProvideFrom() != null){
      if(Objects.nonNull(job.getJobRelated().getMProvideFrom())){
        if(dto.getJobRelated().getProvideFrom() != job.getJobRelated().getMProvideFrom().getId()) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getMProvideFrom())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getProvideTo() != null){
      if(Objects.nonNull(job.getJobRelated().getMProvideTo())){
        if(dto.getJobRelated().getProvideTo() != job.getJobRelated().getMProvideTo().getId()) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getMProvideTo())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getProvideMethod() != null){
      if(Objects.nonNull(job.getJobRelated().getMProvideMethod())){
        if(dto.getJobRelated().getProvideMethod() != job.getJobRelated().getMProvideMethod().getId()) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getMProvideMethod())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getPersonalized() != null){
      if(Objects.nonNull(job.getJobRelated().getMPersonalized())){
        if(dto.getJobRelated().getPersonalized() != job.getJobRelated().getMPersonalized().getId()) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getMPersonalized())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getProvideToDetail() != null){
      if(Objects.nonNull(job.getJobRelated().getMProvideToDetail())){
        if(!dto.getJobRelated().getProvideToDetail().equals(job.getJobRelated().getMProvideToDetail().getId())) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getMProvideToDetail())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getMainPerson() != null){
      if(Objects.nonNull(job.getJobRelated().getMPerson())){
        if(!dto.getJobRelated().getMainPerson().equals(job.getJobRelated().getMPerson().getId()) ) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getMPerson())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getSubPerson1() != null){
      if(Objects.nonNull(job.getJobRelated().getSubPerson1())){
        if(dto.getJobRelated().getSubPerson1() != job.getJobRelated().getSubPerson1().getId()) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getSubPerson1())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getSubPerson2() != null){
      if(Objects.nonNull(job.getJobRelated().getSubPerson2())){
        if(dto.getJobRelated().getSubPerson2() != job.getJobRelated().getSubPerson2().getId()) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getSubPerson2())) {
        updateRpaRelatedFlg = true;
      }
    }

    if(dto.getJobRelated().getSubPerson3() != null){
      if(Objects.nonNull(job.getJobRelated().getSubPerson3())){
        if(dto.getJobRelated().getSubPerson3() != job.getJobRelated().getSubPerson3().getId()) {
          updateRpaRelatedFlg = true;
        }
      } else {
        updateRpaRelatedFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getJobRelated().getSubPerson3())) {
        updateRpaRelatedFlg = true;
      }
    }

    JobRelated jobRelated =
        jobRelatedRepository
            .findById(job.getJobRelated().getId())
            .orElseThrow(
                () -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_NOT_FOUND, id));

    
    if (Objects.nonNull(dto.getJobRelated().getProvideFrom())) {
      MProvideFrom provideFrom =
          mProvideFromRepository
              .findById(dto.getJobRelated().getProvideFrom())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.PROVIDE_FROM_NOT_FOUND,
                          dto.getJobRelated().getProvideFrom()));
      jobRelated.setMProvideFrom(provideFrom);
    } else {
      jobRelated.setMProvideFrom(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getProvideTo())) {
      MProvideTo provideTo =
          mProvideToRepository
              .findById(dto.getJobRelated().getProvideTo())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.PROVIDE_TO_NOT_FOUND,
                          dto.getJobRelated().getProvideFrom()));
      jobRelated.setMProvideTo(provideTo);
    } else {
      jobRelated.setMProvideTo(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getProvideMethod())) {
      MProvideMethod provideMethod =
          mProvideMethodRepository
              .findById(dto.getJobRelated().getProvideMethod())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPROVIDE_METHOD,
                          dto.getJobRelated().getProvideMethod()));
      jobRelated.setMProvideMethod(provideMethod);
    } else {
      jobRelated.setMProvideMethod(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getPersonalized())) {
      MPersonalized mPersonalized =
          mPersonalizedRepository
              .findById(dto.getJobRelated().getPersonalized())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSONALIZEDS,
                          dto.getJobRelated().getPersonalized()));
      jobRelated.setMPersonalized(mPersonalized);
    } else {
      jobRelated.setMPersonalized(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getProvideToDetail())) {
      MProvideToDetail mProvideToDetail =
          mProvideToDetailRepository
              .findById(dto.getJobRelated().getProvideToDetail())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPROVIDE_TO_DETAIL,
                          dto.getJobRelated().getProvideToDetail()));
      jobRelated.setMProvideToDetail(mProvideToDetail);
    } else {
      jobRelated.setMProvideToDetail(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getMainPerson())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getMainPerson())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getMainPerson()));
      jobRelated.setMPerson(mPerson);
    } else {
      jobRelated.setMPerson(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getSubPerson1())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getSubPerson1())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getSubPerson1()));
      jobRelated.setSubPerson1(mPerson);
    } else {
      jobRelated.setSubPerson1(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getSubPerson2())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getSubPerson2())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getSubPerson2()));
      jobRelated.setSubPerson2(mPerson);
    } else {
      jobRelated.setSubPerson2(null);
    }
    if (Objects.nonNull(dto.getJobRelated().getSubPerson3())) {
      MPerson mPerson =
          mPersonsRepository
              .findById(dto.getJobRelated().getSubPerson3())
              .orElseThrow(
                  () ->
                      new ExceptionRpa(
                          HttpStatus.NOT_FOUND,
                          RpaMessage.MPERSON_NOT_FOUND,
                          dto.getJobRelated().getSubPerson3()));
      jobRelated.setSubPerson3(mPerson);
    } else {
      jobRelated.setSubPerson3(null);
    }

    // ============== job_risk
    
    if (dto.getJobRisk().getPaymentMethodId() != null) {
      if (Objects.nonNull(job.getJobRisk().getMRiskPaymentMethod())) {
        if (dto.getJobRisk().getPaymentMethodId() != job.getJobRisk().getMRiskPaymentMethod().getId()) {
          updateRpaRiskFlg = true;
        }
      } else {
        updateRpaRiskFlg = true;
      }
    } else {
      if (Objects.nonNull(job.getJobRisk().getMRiskPaymentMethod())) {
        updateRpaRelatedFlg = true;
      }
    }
    if (dto.getJobRisk().getAmountCategoryId() != null) {
      if (Objects.nonNull(job.getJobRisk().getMRiskAmountCategory())) {
        if (dto.getJobRisk().getPaymentMethodId() != job.getJobRisk().getMRiskAmountCategory().getId()) {
          updateRpaRiskFlg = true;
        }
      } else {
        updateRpaRiskFlg = true;
      }
    } else {
      if (Objects.nonNull(job.getJobRisk().getMRiskAmountCategory())) {
        updateRpaRelatedFlg = true;
      }
    }
    if (dto.getJobRisk().getDataCreationId() != null) {
      if (Objects.nonNull(job.getJobRisk().getMRiskDataCreation())) {
        if (dto.getJobRisk().getPaymentMethodId() != job.getJobRisk().getMRiskDataCreation().getId()) {
          updateRpaRiskFlg = true;
        }
      } else {
        updateRpaRiskFlg = true;
      }
    } else {
      if (Objects.nonNull(job.getJobRisk().getMRiskDataCreation())) {
        updateRpaRelatedFlg = true;
      }
    }

    JobRisk jobRisks =
        jobRiskRepository
            .findById(job.getJobRisk().getId())
            .orElseThrow(
                () -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_RISK_NOT_FOUND, id));

    if (Objects.nonNull(dto.getJobRisk().getPaymentMethodId())) {
      MRiskPaymentMethod mRiskPaymentMethod = paymentMethodRepository
            .findById(dto.getJobRisk().getPaymentMethodId())
              .orElseThrow(
                  () -> new ExceptionRpa(
                      HttpStatus.NOT_FOUND,
                      RpaMessage.PAYMENTMETHOD_NOT_FOUND,
                      dto.getJobRisk().getPaymentMethodId()));
      jobRisks.setMRiskPaymentMethod(mRiskPaymentMethod);
    } else {
      jobRisks.setMRiskPaymentMethod(null);
    }
    if (Objects.nonNull(dto.getJobRisk().getAmountCategoryId())) {
      MRiskAmountCategory mRiskAmountCategory = amountCategoryRepository
          .findById(dto.getJobRisk().getAmountCategoryId())
          .orElseThrow(
              () -> new ExceptionRpa(
                  HttpStatus.NOT_FOUND,
                  RpaMessage.AMOUNTCATEGORY_NOT_FOUND,
                  dto.getJobRisk().getAmountCategoryId()));
      jobRisks.setMRiskAmountCategory(mRiskAmountCategory);
    } else {
      jobRisks.setMRiskAmountCategory(null);
    }
    if (Objects.nonNull(dto.getJobRisk().getDataCreationId())) {
      MRiskDataCreation mRiskDataCreation = dataCreationRepository
          .findById(dto.getJobRisk().getDataCreationId())
          .orElseThrow(
              () -> new ExceptionRpa(
                  HttpStatus.NOT_FOUND,
                  RpaMessage.DATACREATION_NOT_FOUND,
                  dto.getJobRisk().getDataCreationId()));
      jobRisks.setMRiskDataCreation(mRiskDataCreation);
    } else {
      jobRisks.setMRiskDataCreation(null);
    }

    // update all 

    if(dto.getCategory() != job.getCategory()
      || !dto.getName().equals(job.getName())
      || dto.getDescription() != job.getDescription()
      || dto.getLedgerId() != job.getLedgerId()
      ) {
        updateJobFlg = true;
    }

    if(dto.getStatus() != null){
      if(Objects.nonNull(job.getMJobStatuse())){
        if(dto.getStatus() != job.getMJobStatuse().getId()) {
          updateJobFlg = true;
        }
      } else {
        updateJobFlg = true;
      }
    } else {
      if(Objects.nonNull(job.getMJobStatuse())) {
        updateJobFlg = true;
      }
    }

    // JobRelated

    if (dto.getJobRelated().getDifficulty() != job.getJobRelated().getDifficulty()
        || dto.getJobRelated().getManual() != job.getJobRelated().getManual()
        || dto.getJobRelated().getAccessExtension() != job.getJobRelated().getAccessExtension()
        || dto.getJobRelated().getFormat() != job.getJobRelated().getFormat()
        || !Objects.equals(dto.getJobRelated().getRemark1(), job.getJobRelated().getRemark1())
        || !Objects.equals(dto.getJobRelated().getRemark2(), job.getJobRelated().getRemark2())
        || !Objects.equals(dto.getJobRelated().getAttachment1(), job.getJobRelated().getAttachment1())
        || !Objects.equals(dto.getJobRelated().getAttachment2(), job.getJobRelated().getAttachment2())
        || !Objects.equals(dto.getJobRelated().getManualFilePath(), job.getJobRelated().getManualFilePath())) {
      updateRpaRelatedFlg = true;
    }

    // JobRisk
    if (dto.getJobRisk().getRiskLevelJob() != job.getJobRisk().getRiskLevelJob()
        || !Objects.equals(dto.getJobRisk().getAttachment1(), job.getJobRisk().getAttachment1())
        || !Objects.equals(dto.getJobRisk().getAttachment2(), job.getJobRisk().getAttachment2())
        || !Objects.equals(dto.getJobRisk().getManualFilePath1(), job.getJobRisk().getManualFilePath1())
        || !Objects.equals(dto.getJobRisk().getManualFilePath2(), job.getJobRisk().getManualFilePath2())) {
      updateRpaRiskFlg = true;
    }

    //JobAnalyzeTime
    if(dto.getJobAnalyzeTime().getWorkingMonths() != job.getJobAnalyzeTime().getWorkingMonths()){
      updateRpaAnalyzeFlg = true;
    }


    if(updateRpaAnalyzeFlg == true 
      || updateRpaInfoFlg == true
      || updateRpaRelatedFlg == true
      ) {
        updateJobFlg = true;
      }
    if(updateRpaAnalyzeFlg == true) {
      jobAnalyzeTime.setUpdatedUser(updatedUser);
    }

    if(updateRpaInfoFlg == true) {
      jobRpaInfo.setUpdatedUser(updatedUser);
    }
    if(updateRpaRelatedFlg == true) {
      jobRelated.setUpdatedUser(updatedUser);
    }
    if(updateRpaRiskFlg == true){
      jobRisks.setUpdatedUser(updatedUser);
    }
    //update data

    //job analyze
    jobAnalyzeTime.setWorkingMonths(dto.getJobAnalyzeTime().getWorkingMonths());

    // job related
    jobRelated.setDifficulty(dto.getJobRelated().getDifficulty());
    jobRelated.setManual(dto.getJobRelated().getManual());
    jobRelated.setAccessExtension(dto.getJobRelated().getAccessExtension());
    jobRelated.setRemark1(dto.getJobRelated().getRemark1());
    jobRelated.setRemark2(dto.getJobRelated().getRemark2());
    jobRelated.setAttachment1(dto.getJobRelated().getAttachment1());
    jobRelated.setAttachment2(dto.getJobRelated().getAttachment2());
    jobRelated.setManualFilePath(dto.getJobRelated().getManualFilePath());
    jobRelated.setFormat(dto.getJobRelated().getFormat());

    // job risk
    jobRisks.setRiskLevelJob(dto.getJobRisk().getRiskLevelJob());
    jobRisks.setAttachment1(dto.getJobRisk().getAttachment1());
    jobRisks.setAttachment2(dto.getJobRisk().getAttachment2());
    jobRisks.setManualFilePath1(dto.getJobRisk().getManualFilePath1());
    jobRisks.setManualFilePath2(dto.getJobRisk().getManualFilePath2());

    job.setDescription(dto.getDescription());
    job.setName(dto.getName());

    LedgersLink ledgersLink = ledgersLinkService.findLedgerByRpaNumber(job.getId());
    
    if (dto.rpaNumber != null && dto.rpaNumber.length() > 0) {
      Ledger ledger = ledgerService.findLedgerByRpaNumber(dto.rpaNumber);
      if (ledger == null) {
        throw new ExceptionRpa(
                HttpStatus.NOT_FOUND,
                RpaMessage.LEDGER_NOT_FOUND,
                dto.getJobRelated().getSubPerson1());
      } else {
        if (ledgersLink == null) {
          updateJobFlg = true;
          LedgersLink ledgersLinkNew = new LedgersLink();
          ledgersLinkNew.setLedger(ledger);
          ledgersLinkNew.setJob(job);
          ledgersLinkNew.setUpdatedUser(updatedUser);
          ledgersLinkNew.setCreatedUser(updatedUser);
          ledgersLinkRepository.save(ledgersLinkNew);
        } else if (ledgersLink != null ) {
          if(ledgersLink.getLedger() != null && ledger.getId() != ledgersLink.getLedger().getId()){
            updateJobFlg = true;
            ledgersLink.setIs_deleted(RpaConstant.IS_NOT_DELETE);
            ledgersLink.setLedger(ledger);
            ledgersLink.setUpdatedUser(updatedUser);
            ledgersLinkRepository.save(ledgersLink);
          } else if (ledgersLink.getLedger() == null){
            updateJobFlg = true;
            ledgersLink.setIs_deleted(RpaConstant.IS_NOT_DELETE);
            ledgersLink.setLedger(ledger);
            ledgersLink.setUpdatedUser(updatedUser);
            ledgersLinkRepository.save(ledgersLink);
          }
        }
      }
    } else {
      if (ledgersLink != null) {
        LedgersLink ledgersLinkUp =
            ledgersLinkRepository
                .findById(ledgersLink.getId())
                .orElseThrow(
                    () -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_NOT_FOUND, id));
        updateJobFlg = true;
        ledgersLinkUp.setIs_deleted(RpaConstant.IS_DELETE);
        ledgersLinkUp.setUpdatedAt(LocalDateTime.now());
        ledgersLinkUp.setLedger(null);
        ledgersLinkUp.setUpdatedUser(updatedUser);
        ledgersLinkRepository.save(ledgersLinkUp);
      }
    }

    if(dto.getJobRelated().getPathAttachment1Del() != null){
      String pathAttachment1Del = dto.getJobRelated().getPathAttachment1Del();
      String[] split = pathAttachment1Del.split("/");
      String subFolderAttachment1 = split[0];
      String fileNameAttachment1 = split[1];
      fileStorageService.deleteFile("rpa_spec", subFolderAttachment1, fileNameAttachment1, 1);
    }
    if(dto.getJobRelated().getPathAttachment2Del() != null){
      String pathAttachment2Del = dto.getJobRelated().getPathAttachment2Del();
      String[] split = pathAttachment2Del.split("/");
      String subFolderAttachment2 = split[0];
      String fileNameAttachment2 = split[1];
      fileStorageService.deleteFile("rpa_spec", subFolderAttachment2, fileNameAttachment2, 2);
    }

      if(dto.getJobRisk().getPathAttachment1Del() != null){
      String pathAttachment1Del = dto.getJobRisk().getPathAttachment1Del();
      String[] split = pathAttachment1Del.split("/");
      String subFolderAttachment1 = split[0];
      String fileNameAttachment1 = split[1];
      fileStorageService.deleteFile("rpa_spec", subFolderAttachment1, fileNameAttachment1, 3);
    }
    if(dto.getJobRisk().getPathAttachment2Del() != null){
      String pathAttachment2Del = dto.getJobRisk().getPathAttachment2Del();
      String[] split = pathAttachment2Del.split("/");
      String subFolderAttachment2 = split[0];
      String fileNameAttachment2 = split[1];
      fileStorageService.deleteFile("rpa_spec", subFolderAttachment2, fileNameAttachment2, 4);
    }

    if(updateJobFlg == true) {
      job.setUpdatedUser(updatedUser);
      job.setUpdatedAt(updateAt);
    }
    jobRepository.save(job);
    
    jobRpaInfoRepository.save(jobRpaInfo);
    jobAnalyzeTimeRepository.save(jobAnalyzeTime);
    jobRelatedRepository.save(jobRelated);
    jobRiskRepository.save(jobRisks);
    // } catch (Exception ex) {
    // LOG.info(String.format("Update job Exception: %s", ex));
    // }
    return job;
  }

  // Delete jobs & related
  @Transactional
  public boolean deleteById(int id) {
    Job job =
        jobRepository
            .findById(id)
            .orElseThrow(
                () -> new ExceptionRpa(HttpStatus.NOT_FOUND, RpaMessage.JOB_NOT_FOUND, id));
    job.setIsDeleted(RpaConstant.IS_DELETE);

    if (Objects.nonNull(job.getJobAnalyzeTime())) {
      job.getJobAnalyzeTime().setIsDeleted(RpaConstant.IS_DELETE);
    }
    if (Objects.nonNull(job.getJobRelated())) {
      job.getJobRelated().setIsDeleted(RpaConstant.IS_DELETE);
    }
    if (Objects.nonNull(job.getJobRpaInfo())) {
      job.getJobRpaInfo().setIsDeleted(RpaConstant.IS_DELETE);
    }

    jobRepository.save(job);
    return true;
  }

  public List<JobListDto> selectColumn(String column) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("select distinct job." + column + " from ")
          .append(
              "(select "
                  + "jobs.id, jobs.number, jobs.name, ledgers.id as ledgerId, ledgers.rpa_number as rpaNumber, m_departments.name as departmentName, jobs.category, "
                  + "m_persons.person_name as personName, job_rpa_infos.consignee as consigneeName, m_job_statuses.name as jobStatusesName, TO_CHAR(jobs.updated_at, 'YYYY-MM-DD HH24:MI:SS') as updatedAt "
                  + "from jobs "
                  + "left join ledgers_link on ledgers_link.job_id = jobs.id and ledgers_link.is_deleted=:isNotDelete "
                  + "left join ledgers on ledgers.id = ledgers_link.ledger_id and ledgers.is_deleted=:isNotDelete "
                  + "left join m_departments on m_departments.id = jobs.department and m_departments.is_deleted=:isNotDelete "
                  + "left join job_rpa_infos on job_rpa_infos.job_id = jobs.id and job_rpa_infos.is_deleted=:isNotDelete "
                  + "left join m_persons on m_persons.id = job_rpa_infos.person and job_rpa_infos.is_deleted=:isNotDelete "
                  // + "left join m_consignees on m_consignees.id = job_rpa_infos.consignee and
                  // m_consignees.is_deleted=:isNotDelete "
                  + "left join m_job_statuses on m_job_statuses.id = jobs.status and m_job_statuses.is_deleted=:isNotDelete "
                  + "where jobs.is_deleted = :isNotDelete ")
          .append(") as job where ")
          .append(" job." + column + " is not null ");
      HashMap<String, Object> params = new HashMap<>();
      params.put("isNotDelete", RpaConstant.IS_NOT_DELETE);

      return (List<JobListDto>) baseCustomRepository.getListData(sql, params, JobListDto.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  @Transactional
  public void assignLedgerByName(String name, int ledgerId) {
    if (name == null) {
      return;
    }
    Job job = jobRepository.findByName(name);
    if (job != null) {
      job.setLedgerId(ledgerId);
      jobRepository.save(job);
    }
  }

  @Transactional
  public void deleteRelatedLedger(String name) {
    if (name == null) {
      return;
    }
    Job job = jobRepository.findByName(name);
    if (job != null) {
      job.setLedgerId(null);
      jobRepository.save(job);
    }
  }

  public boolean checkNumberIsExist(String number, Optional<Integer> id) {
    if (id.isPresent()) {
      return jobRepository.existsByNumberAndIdNot(number, id.get());
    }
    return jobRepository.existsByNumber(number);
  }

  public String generateJobNumber() {
    return jobRepository.jobNumberGenerate();
  }

  public void jobChangeStatus(Integer status, Integer ledgerId) {
    // if (status == 1) { // restore
    //   Ledger ledger = ledgerService.getById(ledgerId);
    //   if (Objects.nonNull(ledger)) {
    //     MStatus mStatus =
    //         mStatusRepository
    //             .findById(Ledger.STATUS_IN_USE)
    //             .orElseThrow(
    //                 () ->
    //                     new ExceptionRpa(
    //                         HttpStatus.NOT_FOUND,
    //                         RpaMessage.MSTATUSES_NOT_FOUND,
    //                         Ledger.STATUS_IN_USE));
    //     ledger.setMstatus(mStatus);
    //   }
    //   // Undo delete exec_dates, exec_days, schedules, xml_schedules
    //   execDateRepository.restoreByLedger(ledgerId);

    //   ExecDay execDay = execDayRepository.findByLedgerId(ledgerId);
    //   if (Objects.nonNull(execDay)) {
    //     execDay.setIsDeleted((short) 0);
    //     execDayRepository.save(execDay);
    //   }

    //   schedulesRepository.restoreByLedger(ledgerId);

    //   XmlSchedule xmlSchedule = xmlScheduleRepository.findByLedgerId(ledgerId);
    //   if (Objects.nonNull(xmlSchedule)) {
    //     xmlSchedule.setIsDeleted((short) 0);
    //     xmlScheduleRepository.save(xmlSchedule);
    //   }
    // }
    String updatedUser = personService.getDetailPerson().getPersonNumber();
    if (status == 2) { // delete
      Ledger ledger = ledgerService.getById(ledgerId);
      if (Objects.nonNull(ledger)) {
        MStatus mStatus =
            mStatusRepository
                .findById(Ledger.STATUS_IN_PENDING)
                .orElseThrow(
                    () ->
                        new ExceptionRpa(
                            HttpStatus.NOT_FOUND,
                            RpaMessage.MSTATUSES_NOT_FOUND,
                            Ledger.STATUS_IN_PENDING));
        ledger.setMstatus(mStatus);
      }
      // Soft delete exec_dates, exec_days, schedules, xml_schedules
      List<ExecDate> execDates =
          execDateRepository.getExecDateByLedgerIdAndIsDeleted(
              ledgerId, (short) RpaConstant.IS_NOT_DELETE);
      for (ExecDate execDate : execDates) {
        execDate.setIsDeleted((short) RpaConstant.IS_DELETE);
        execDate.setUpdatedUser(updatedUser);
        execDateRepository.save(execDate);
      }

      ExecDay execDay = execDayRepository.findByLedgerId(ledgerId);
      if (Objects.nonNull(execDay)) {
        execDay.setIsDeleted((short) RpaConstant.IS_DELETE);
        execDay.setUpdatedUser(updatedUser);
        execDay.setCreatedUser(updatedUser);
        execDayRepository.save(execDay);
      }

      LocalDate now = LocalDate.now();
      // schedulesRepository.deleteByLedgerIdAndExecDate(ledgerId, LocalDate.now());
      schedulesRepository.deleteScheduleByLedgerAndExecDateGreater(ledgerId, now);

      // XmlSchedule xmlSchedule = xmlScheduleRepository.findByLedgerId(ledgerId);
      // if (Objects.nonNull(xmlSchedule)) {
      //   xmlSchedule.setIsDeleted((short) RpaConstant.IS_DELETE);
      //   xmlScheduleRepository.save(xmlSchedule);
      // }
      xmlScheduleRepository.deleteXmlScheduleByLedger(ledgerId);

    }
  }

  public Integer checkDepartment(Integer deptId) {
    MPerson person = personService.getDetailPerson();
    Integer role = person.getMRole().getId();
    Integer departmentUser = null;
    if(Objects.nonNull(person.getMDepartment())){
      departmentUser = person.getMDepartment().getId();
    }

    if(role.equals(1)){
      return 1;
    } else {
      if(deptId == departmentUser) {
        return 2;
      }
      return 3;
    }

  }
}
