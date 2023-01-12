@echo on
@echo ====================================================================================
@echo $ code: https://gitee.com/owenwangwen/open-capacity-platform  					$
@echo $ doc: https://www.kancloud.cn/owenwangwen/open-capacity-platform  				$
@echo $ blog: https://blog.51cto.com/13005375   										$
@echo ====================================================================================

call mvn clean package -Dmaven.test.skip=true

pause