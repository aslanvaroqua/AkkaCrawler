import com.wishclouds.Worker
import com.wishclouds.WorkExecutor
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * User: aslanvaroqua
 * Date: 10/18/13
 * Time: 4:29 PM
 */
class ExecuteCommandTest extends FunSuite with ShouldMatchers{

  test ("execute a command") {
    val hi =  Worker.exec2out("echo \"hi\"")
    hi should be === "hi"

  }

}
