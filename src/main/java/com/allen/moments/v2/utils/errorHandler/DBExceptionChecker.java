package com.allen.moments.v2.utils.errorHandler;

import com.allen.moments.v2.model.DML;
import com.allen.moments.v2.model.ErrorType;
import com.allen.moments.v2.utils.ApplicationException;

public class DBExceptionChecker {
    public static void checkIfRowsAffectedIsOne(int rowsAffected, ErrorType zeroRowAffectedErrorType) throws Exception {
        if (rowsAffected == 0) {
            throw new ApplicationException(zeroRowAffectedErrorType.errNo, zeroRowAffectedErrorType.message);
        }
        if (rowsAffected > 1) {
            throw new ApplicationException(ErrorType.DIRTY_DATA.errNo, ErrorType.DIRTY_DATA.message);
        }
    }

    public static void checkIfRowsAffectedIsOne(int rowsAffected, DML dml) throws Exception {
        if (rowsAffected == 0) {
            throw new ApplicationException(ErrorType.DML_ERR.errNo, ErrorType.DML_ERR.message + ": " + dml.description);
        }
        if (rowsAffected > 1) {
            throw new ApplicationException(ErrorType.DIRTY_DATA.errNo, ErrorType.DIRTY_DATA.message);
        }
    }
}
