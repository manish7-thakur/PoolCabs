/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var step=1
function slideShow(){
    //if browser does not support the image object, exit.
    if (!document.images)
        return;
    document.images.slide.src=eval("image"+step+".src")
    if (step < 3)
        step++;
    else
        step=1;
    //call function "slideit()" every 2.5 seconds
    setTimeout("slideShow()",2500);
}