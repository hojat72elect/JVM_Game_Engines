/*
For more info, go to http://bulletphysics.org
*/

#ifndef BT_GLUT_INCLUDE_H
#define BT_GLUT_INCLUDE_H


#ifdef _WIN32//for glut.h
#include <windows.h>
#endif

//think different
#if defined(__APPLE__) && !defined (VMDMESA)
#include <OpenGL/OpenGL.h>
#include <OpenGL/gl.h>
#include <OpenGL/glu.h>
#include <GLUT/glut.h>
#else


#ifdef _WINDOWS
#include <windows.h>
#include <GL/gl.h>
#include <GL/glu.h>
#else
#include <GL/gl.h>
#include <GL/glut.h>
#endif //_WINDOWS
#endif //APPLE

#endif //BT_GLUT_INCLUDE_H
