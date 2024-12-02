//package reservation.Utils;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import reservation.Entity.User.Role;
//
//@Converter(autoApply = true)
//public class RoleConverter implements AttributeConverter<Role, String> {
//
//	@Override
//	public String convertToDatabaseColumn(Role role) {
//		return (role != null) ? role.getDbValue() : null;
//	}
//	
//    @Override
//    public Role convertToEntityAttribute(String dbValue) {
//        return (dbValue != null) ? Role.fromDbValue(dbValue) : null;
//    }
//}
