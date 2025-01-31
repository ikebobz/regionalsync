package application;

public class Queries {

	public static final String ISSUE1 ="update hiv_observation t1 set archived = 1\r\n"
			+ "WHERE  EXISTS (\r\n"
			+ "   SELECT * FROM hiv_observation t2 inner join patient_person p on p.uuid = t2.person_uuid\r\n"
			+ "   WHERE  t1.person_uuid = t2.person_uuid AND t1.type = t2.type\r\n"
			+ "   AND    t1.ctid < t2.ctid and hospital_number ilike ?\r\n"
			+ "   )";
	public static final String ISSUE2 ="with data as (select distinct on(person_uuid ) person_uuid,h.uuid,first_name,other_name,surname,date_of_observation from hiv_observation h inner join patient_person p\r\n"
			+ "on p.uuid = h.person_uuid where first_name ilike ? and surname ilike ? order by 1,6 desc)\r\n"
			+ "delete from hiv_observation where uuid in (select uuid from data)";
	public static final String ISSUE3 ="update patient_visit t1 set visit_end_date = visit_start_date\r\n"
			+ "WHERE  EXISTS (\r\n"
			+ "   SELECT * FROM patient_visit t2\r\n"
			+ "   WHERE  t1.person_uuid = t2.person_uuid AND t1.visit_start_date = t2.visit_start_date and t2.visit_end_date is null\r\n"
			+ "   AND  t1.ctid < t2.ctid\r\n"
			+ "   )";
	public static final String ISSUE4 ="delete from base_organisation_unit_identifier where length(code) <= 3;\r\n"
			+ "update ndr_code_set set sys_description = 'HIV_CODE' where code = '86406008' and code_set_nm = 'CONDITION_CODE';\r\n"
			+ "update ndr_code_set set sys_description = 'HIV' where code_description = 'HIV' and code_set_nm = 'PROGRAM_AREA';\r\n"
			+ "update ndr_code_set set code_description = 'Suru' where code = '695';\r\n"
			+ "update base_application_codeset set display = 'No signs or symptoms of TB' where id = 67;\r\n"
			+ "update base_application_codeset set display = 'Presumptive TB and referred for evaluation' where id =  68;\r\n"
			+ "\r\n"
			+ "update hiv_art_pharmacy set ipt = '{\"type\":\"null\",\"dateCompleted\":\"null\"}'::jsonb where ipt is null and extra->>'regimens' ilike '%soni%';\r\n"
			+ "\r\n"
			+ "----------solution that targets specific clients----------------------------\r\n"
			+ "with initial_inh_disp as (select person_uuid,min(visit_date) as visit from hiv_art_pharmacy where extra->>'regimens' ilike '%sonia%' group by 1)\r\n"
			+ "update hiv_art_pharmacy hap set ipt = ipt || '{\"type\":\"START_INITIATION\",\"dateCompleted\":\"null\"}' from initial_inh_disp iid where hap.person_uuid\r\n"
			+ "=iid.person_uuid and hap.visit_date = iid.visit;\r\n"
			+ "-------correct weights,heights and blank next-appointments----------\r\n"
			+ "update triage_vital_sign set height = 199 where height > 200;\r\n"
			+ "\r\n"
			+ "update triage_vital_sign set body_weight = 199 where body_weight > 200;\r\n"
			+ "\r\n"
			+ "update hiv_art_pharmacy set next_appointment = visit_date + refill_period where refill_period is not null\r\n"
			+ "and next_appointment is null;\r\n"
			+ "------------remove duplicates from the ndr code sets and regimen resolvers---------------------------\r\n"
			+ "\r\n"
			+ "DELETE FROM hiv_regimen_resolver t1\r\n"
			+ "WHERE  EXISTS (\r\n"
			+ "   SELECT FROM hiv_regimen_resolver t2\r\n"
			+ "   WHERE  t1.regimensys = t2.regimensys and t1.ctid < t2.ctid\r\n"
			+ "   );\r\n"
			+ " update ndr_code_set t1 set code_description = code_description || '_old'\r\n"
			+ "WHERE  EXISTS (\r\n"
			+ "   SELECT FROM ndr_code_set t2\r\n"
			+ "   WHERE  t1.code_description = t2.code_description and t1.code_set_nm = t2.code_set_nm \r\n"
			+ "   AND  t1.ctid < t2.ctid\r\n"
			+ "   ) and t1.code_set_nm = 'ARV_REGIMEN';\r\n"
			+ "  -----fix status misclassification-----from last script-------------------- \r\n"
			+ "update hiv_status_tracker set hiv_status = 'ART_TRANSFER_OUT' where hiv_status ilike '%ART_TRANSFER_OUT;ART_%';\r\n"
			+ "update hiv_status_tracker set hiv_status = 'STOPPED_TREATMENT' where hiv_status ilike '%STOPPED_TREATMENT;STOP%';\r\n"
			+ "update hiv_status_tracker set hiv_status = 'KNOWN_DEATH' where hiv_status ilike '%KNOWN_DEATH;KNO%';\r\n"
			+ "\r\n"
			+ "----resolve hts xml issues-----------------------------------\r\n"
			+ "update hts_client hc1 set archived = 1 where exists\r\n"
			+ "(select * from hts_client hc2 where hc1.client_code = hc2.client_code and hc1.ctid < hc2.ctid);\r\n"
			+ "update ndr_code_set set code_description = 'Lagos',sys_description = 'Lagos' where code = '25' and code_set_nm = 'STATES';\r\n"
			+ "";
	public static final String ISSUE5 ="Update hiv_art_pharmacy set archived = 1 where person_uuid in (select uuid from patient_person where hospital_number ilike ?)and visit_date = ?";
	public static final String ISSUE6 ="delete from hiv_art_pharmacy where refill_period is null and next_appointment is null";
	public static final String ISSUE7 = "delete from ndr_message_log where file_type='recaptured-biometric'";
	public static final String ISSUE8 = "with targetuuid as (select hst.uuid from patient_person p inner join\r\n"
			+ "hiv_status_tracker hst on p.uuid = hst.person_id where hospital_number = ? order by status_date desc limit 1)\r\n"
			+ "update hiv_status_tracker st set archived = ? from targetuuid t\r\n"
			+ "where st.uuid = t.uuid";
	public static final String unArchiveStatus = "with status as (select uuid from (select hospital_number,hst.uuid,hiv_status,status_date,hst.archived,row_number() over (partition by hospital_number order by status_date desc) rnk from patient_person p inner join\r\n"
			+ "hiv_status_tracker hst on p.uuid = hst.person_id where hospital_number = ? ) hivstatus where rnk = 2)\r\n"
			+ "update hiv_status_tracker st set archived = ? from status t\r\n"
			+ "where st.uuid = t.uuid";
}
