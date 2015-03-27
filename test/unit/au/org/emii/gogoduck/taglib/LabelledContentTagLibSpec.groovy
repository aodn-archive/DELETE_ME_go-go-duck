package au.org.emii.gogoduck.taglib



import go.go.duck.LabelledContentTagLib;
import grails.test.mixin.*

import org.junit.*

import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(LabelledContentTagLib)
class LabelledContentTagLibSpec extends Specification {

    def "display labelled content unconditionally"() {
        when:
        String string = applyTemplate "<g:labelledContent labelCode=\"some.code\">a value</g:labelledContent>"

        then:
        string == '<dt>some.code:</dt>\n<dd>a value</dd>\n'
   }

    @Unroll("display labelled content #test")
    def "display labelled content conditionally"() {
        when:
        String string = applyTemplate "<g:labelledContent if=\"${condition}\" labelCode=\"some.code\">a value</g:labelledContent>"

        then:
        string == expectedOutput

        where:
        test                        | condition   | expectedOutput
        'includes content'          | 'some text' | '<dt>some.code:</dt>\n<dd>a value</dd>\n'
        'doesn\'t include content'  | ''          | ''
    }
}
