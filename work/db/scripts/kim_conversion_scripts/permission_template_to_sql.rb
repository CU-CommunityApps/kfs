require 'kfsdb'
require 'digest/sha1'
#require 'mysql-connector-java-5.0.5-bin.jar'
include_class 'com.mysql.jdbc.Driver'

INPUT_FILE = "permission_templates.txt"

def generate_obj_id(table_name, unique_name)
	Digest::SHA1.hexdigest("#{table_name}:#{unique_name}")[0..32]
end

def format_count(count)
	return "#{"%02d" % count}"
end

def generate_template_id(count)
	"KFS-PERM-TMPL#{format_count(count)}"
end

def retrieve_types_map()
	types = {}
	db_connect("sample_db") do |con|
		con.query("select kim_type_id, type_nm from kr_kim_type_t") do |row|
			types[row.getString("type_nm")] = row.getString("kim_type_id")
		end
	end
	types
end

def generate_permission_template_sql(id_count, template_name, description, type_id, namespace_cd)
	table_name = "kr_kim_perm_tmpl_t"
	obj_id = generate_obj_id(table_name, template_name)
	"insert into #{table_name} (perm_tmpl_id, obj_id, ver_nbr, name, description, kim_type_id, actv_ind, namespace_cd)\n\tvalues ('#{generate_template_id(id_count)}', '#{obj_id}', 0, '#{template_name.gsub(/'/,"''")}', '#{description[0..36].gsub(/'/,"''")}', '#{type_id}', 'Y', #{namespace_cd})\n/\n"
end

permission_types = retrieve_types_map
File.open(INPUT_FILE, "r") do |fin|
	template_count = 1
	fin.each_line do |line|
		name, description, type_name = line.strip.split(/::/)
		namespace = name.strip.split(/ /,2)
#		puts namespace
		description = "" if description.nil?
		type_id = ""
		if !type_name.nil? && type_name.strip.length > 0
			type_id = permission_types[type_name.strip]
		end
		puts generate_permission_template_sql(template_count, namespace[1].strip, description.strip, type_id, namespace[0].strip)
		template_count += 1
	end
end